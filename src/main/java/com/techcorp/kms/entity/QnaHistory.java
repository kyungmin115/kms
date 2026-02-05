package com.techcorp.kms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "qna_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QnaHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question; // 질문

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer; // 답변

    @Column(columnDefinition = "TEXT")
    private String sourceDocuments;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "feedback")
    private Integer feedback;



}
