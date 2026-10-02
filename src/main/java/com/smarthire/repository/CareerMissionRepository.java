package com.smarthire.repository;

import com.smarthire.entity.CareerMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CareerMissionRepository extends JpaRepository<CareerMission, Long> {
    List<CareerMission> findByStudentIdOrderByDateAssignedDesc(Long studentId);
    List<CareerMission> findByStudentIdAndCompletedFalse(Long studentId);
}
