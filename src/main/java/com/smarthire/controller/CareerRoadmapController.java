package com.smarthire.controller;

import com.smarthire.entity.CareerRoadmap;
import com.smarthire.entity.RoadmapNode;
import com.smarthire.entity.StudentProfile;
import com.smarthire.entity.User;
import com.smarthire.repository.StudentProfileRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.CareerRoadmapService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roadmap")
public class CareerRoadmapController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CareerRoadmapService roadmapService;

    public CareerRoadmapController(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            CareerRoadmapService roadmapService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.roadmapService = roadmapService;
    }

    private StudentProfile getStudent(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    @GetMapping
    public ResponseEntity<CareerRoadmap> getRoadmap(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(roadmapService.getOrGenerateRoadmap(student.getId()));
    }

    @PatchMapping("/nodes/{nodeId}/status")
    public ResponseEntity<RoadmapNode> updateNodeStatus(
            @PathVariable Long nodeId,
            @RequestParam String status) {
        return ResponseEntity.ok(roadmapService.updateNodeStatus(nodeId, status));
    }
}
