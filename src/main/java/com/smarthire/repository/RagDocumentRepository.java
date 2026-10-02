package com.smarthire.repository;

import com.smarthire.entity.RagDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RagDocumentRepository extends JpaRepository<RagDocument, Long> {
    Optional<RagDocument> findByDocumentId(String documentId);
    List<RagDocument> findByRecruiterEmailOrderByUploadedAtDesc(String recruiterEmail);
    List<RagDocument> findAllByOrderByUploadedAtDesc();
    boolean existsByFilenameAndRecruiterEmail(String filename, String recruiterEmail);
}
