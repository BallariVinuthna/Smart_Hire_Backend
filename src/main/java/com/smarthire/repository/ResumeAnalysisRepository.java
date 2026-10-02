package com.smarthire.repository;

import com.smarthire.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {
    Optional<ResumeAnalysis> findFirstByResumeIdOrderByAnalyzedAtDesc(Long resumeId);
}
