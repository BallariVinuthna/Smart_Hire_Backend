package com.smarthire.repository;

import com.smarthire.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentIdOrderByAppliedAtDesc(Long studentId);
    List<Application> findByJobIdOrderByAppliedAtDesc(Long jobId);
    List<Application> findByJobCompanyIdOrderByAppliedAtDesc(Long companyId);
    Optional<Application> findByStudentIdAndJobId(Long studentId, Long jobId);
    Long countByStatus(String status);
}
