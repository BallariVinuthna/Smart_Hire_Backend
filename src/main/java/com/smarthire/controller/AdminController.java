package com.smarthire.controller;

import com.smarthire.entity.User;
import com.smarthire.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final PlacementDriveRepository placementDriveRepository;

    public AdminController(
            UserRepository userRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            PlacementDriveRepository placementDriveRepository) {
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.placementDriveRepository = placementDriveRepository;
    }

    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getOverview() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("totalUsers", userRepository.count());
        data.put("totalJobs", jobRepository.count());
        data.put("totalApplications", applicationRepository.count());
        data.put("totalPlacementDrives", placementDriveRepository.count());
        data.put("aiServicesStatus", "HEALTHY");
        data.put("aiInferences24h", 1420);
        data.put("truthCheckVerifications", 894);
        data.put("readinessEvaluationsRun", 3280);
        return ResponseEntity.ok(data);
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}
