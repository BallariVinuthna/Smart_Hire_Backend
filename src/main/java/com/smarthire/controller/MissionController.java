package com.smarthire.controller;

import com.smarthire.entity.CareerMission;
import com.smarthire.entity.StudentProfile;
import com.smarthire.entity.User;
import com.smarthire.repository.StudentProfileRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.MissionGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MissionGenerationService missionService;

    public MissionController(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            MissionGenerationService missionService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.missionService = missionService;
    }

    private StudentProfile getStudent(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    @GetMapping("/daily")
    public ResponseEntity<List<CareerMission>> getDailyMissions(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(missionService.getDailyMissions(student.getId()));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<CareerMission> completeMission(@PathVariable Long id) {
        return ResponseEntity.ok(missionService.completeMission(id));
    }
}
