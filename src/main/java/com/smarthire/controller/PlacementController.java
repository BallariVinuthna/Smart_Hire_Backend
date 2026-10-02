package com.smarthire.controller;

import com.smarthire.dto.PlacementDtos.BatchStatsResponse;
import com.smarthire.entity.*;
import com.smarthire.repository.*;
import com.smarthire.service.PlacementIntelligenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/placement")
public class PlacementController {

    private final PlacementDriveRepository placementDriveRepository;
    private final PlacementApplicationRepository placementApplicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final PlacementIntelligenceService placementIntelligenceService;

    public PlacementController(
            PlacementDriveRepository placementDriveRepository,
            PlacementApplicationRepository placementApplicationRepository,
            StudentProfileRepository studentProfileRepository,
            UserRepository userRepository,
            PlacementIntelligenceService placementIntelligenceService) {
        this.placementDriveRepository = placementDriveRepository;
        this.placementApplicationRepository = placementApplicationRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.userRepository = userRepository;
        this.placementIntelligenceService = placementIntelligenceService;
    }

    @GetMapping("/batch-stats")
    public ResponseEntity<BatchStatsResponse> getBatchStats(@RequestParam(defaultValue = "2027") Integer batchYear) {
        return ResponseEntity.ok(placementIntelligenceService.getBatchStatistics(batchYear));
    }

    @GetMapping("/drives")
    public ResponseEntity<List<PlacementDrive>> getDrives() {
        return ResponseEntity.ok(placementDriveRepository.findAll());
    }

    @PostMapping("/drives")
    public ResponseEntity<PlacementDrive> createDrive(@RequestBody PlacementDrive drive) {
        return ResponseEntity.ok(placementDriveRepository.save(drive));
    }

    @PostMapping("/drives/{id}/register")
    public ResponseEntity<PlacementApplication> registerForDrive(Authentication auth, @PathVariable Long id) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        StudentProfile student = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
        PlacementDrive drive = placementDriveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drive not found"));

        PlacementApplication app = placementApplicationRepository.findByPlacementDriveIdAndStudentId(id, student.getId())
                .orElse(new PlacementApplication(drive, student));

        return ResponseEntity.ok(placementApplicationRepository.save(app));
    }

    @GetMapping("/students")
    public ResponseEntity<List<StudentProfile>> getStudents(@RequestParam(defaultValue = "2027") Integer batchYear) {
        return ResponseEntity.ok(studentProfileRepository.findByBatchYear(batchYear));
    }
}
