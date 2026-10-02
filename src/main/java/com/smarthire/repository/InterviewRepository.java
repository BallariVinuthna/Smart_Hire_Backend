package com.smarthire.repository;

import com.smarthire.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByStudentIdOrderByCreatedAtDesc(Long studentId);
    Optional<Interview> findFirstByStudentIdAndStatusOrderByCreatedAtDesc(Long studentId, String status);
}
