package com.smarthire.repository;

import com.smarthire.entity.CareerReadiness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CareerReadinessRepository extends JpaRepository<CareerReadiness, Long> {
    List<CareerReadiness> findByStudentIdOrderByEvaluatedAtDesc(Long studentId);
    Optional<CareerReadiness> findFirstByStudentIdOrderByEvaluatedAtDesc(Long studentId);
}
