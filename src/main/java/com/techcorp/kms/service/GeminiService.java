package com.techcorp.kms.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class GeminiService {

    private final View error;
    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public GeminiService(View error) {
        // 타임아웃 시간 늘리기
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        this.error = error;
    }

    //qna 답변
    public String generateAnswer(String question, String relevantDocs) throws IOException {
        String systemPrompt = "당신은 기업 내부 지식 관리 시스템의 AI 어시스턴트입니다. " +
                "제공된 문서를 기반으로 정확하고 친절하게 답변하세요. " +
                "답변 시 다음 규칙을 따르세요:\n" +
                "1. 제공된 문서 내용만을 기반으로 답변하세요.\n" +
                "2. 문서에 없는 내용은 '제공된 문서에서는 해당 정보를 찾을 수 없습니다'라고 답하세요.\n" +
                "3. 답변은 친절하고 명확하게 작성하세요.\n" +
                "4. 필요시 단계별로 설명하세요.\n\n";

        String prompt = systemPrompt +
                "=== 관련 문서 내용 ===\n" +
                relevantDocs + "\n\n" +
                "=== 사용자 질문 ===\n" +
                question + "\n\n" +
                "답변 : ";

        // api  요청
        JsonObject requestBody = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();

        part.addProperty("text", prompt);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);

        requestBody.add("contents", contents);

        JsonArray safetySettings = new JsonArray();
        String[] categories = {
                "HARM_CATEGORY_HARASSMENT",
                "HARM_CATEGORY_HATE_SPEECH",
                "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                "HARM_CATEGORY_DANGEROUS_CONTENT"
        };

        for (String category : categories) {
            JsonObject setting = new JsonObject();
            setting.addProperty("category", category);
            setting.addProperty("threshold", "BLOCK_NONE");
            safetySettings.add(setting);
        }
        requestBody.add("safetySettings", safetySettings);

        JsonObject generationConfig = new JsonObject();
        generationConfig.addProperty("temperature", 0.3);
        generationConfig.addProperty("topK", 40);
        generationConfig.addProperty("topP", 0.95);
        generationConfig.addProperty("maxOutputTokens", 2048);
        requestBody.add("generationConfig", generationConfig);

        RequestBody body = RequestBody.create(
                requestBody.toString(), MediaType.parse("application/json")
        );

        String urlWithKey = API_URL + "?key=" + apiKey;

        Request request = new Request.Builder()
                .url(urlWithKey)
                .addHeader("Content-Type", "applcation/json")
                .post(body)
                .build();

        try (Response response  = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "No error Body";
                throw new IOException("Gemini API 호출 실패 : " + response.code() + "-" + errorBody);
            }

            String responseBody = response.body().string();
            JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

            return jsonResponse
                    .getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();
        }
    }



    // 벡터 디비 용 추후 구현
//    public float[] generateEmbedding(String text) throws IOException {
//        throw new UnsupportedOperationException("");
//    }


}
