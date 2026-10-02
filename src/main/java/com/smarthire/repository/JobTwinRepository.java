package com.smarthire.repository;

import com.smarthire.entity.JobTwin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JobTwinRepository extends JpaRepository<JobTwin, Long> {
    Optional<JobTwin> findByTargetRole(String targetRole);
    Optional<JobTwin> findByTargetRoleIgnoreCase(String targetRole);
}
