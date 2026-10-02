package com.smarthire.repository;

import com.smarthire.entity.JobMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobMatchRepository extends JpaRepository<JobMatch, Long> {
    List<JobMatch> findByStudentId(Long studentId);
    Optional<JobMatch> findByStudentIdAndJobId(Long studentId, Long jobId);
}
