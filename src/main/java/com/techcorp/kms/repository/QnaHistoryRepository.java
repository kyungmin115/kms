package com.techcorp.kms.repository;

import com.techcorp.kms.entity.QnaHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QnaHistoryRepository extends JpaRepository<QnaHistory, Long> {

    // 사용자별 질문, 히스토리
    List<QnaHistory> findByUserId(String userId);

    // 최근 질문
    List<QnaHistory> findTop10ByOrderByCreatedAtDesc();

    // 특정기간 질문
    List<QnaHistory> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 피드백 점수별
    List<QnaHistory> findByFeedback(Integer feedback);

}
