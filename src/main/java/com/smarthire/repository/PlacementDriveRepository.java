package com.smarthire.repository;

import com.smarthire.entity.PlacementDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {
    List<PlacementDrive> findByBatchYear(Integer batchYear);
    List<PlacementDrive> findByStatus(String status);
}
