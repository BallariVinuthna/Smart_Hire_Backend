package com.smarthire.repository;

import com.smarthire.entity.InterviewResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewResponseRepository extends JpaRepository<InterviewResponse, Long> {
    List<InterviewResponse> findByQuestionInterviewId(Long interviewId);
    Optional<InterviewResponse> findByQuestionId(Long questionId);
}
