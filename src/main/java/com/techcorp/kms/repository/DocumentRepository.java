package com.techcorp.kms.repository;

import com.techcorp.kms.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // 파일명
    Optional<Document> findByFileName(String fileName);

    // 카테고리
    List<Document> findByCategory(String category);

    // 부서
    List<Document> findByDepartment(String department);

    // 파일 타입
    List<Document> findByFileType(String fileType);

}
