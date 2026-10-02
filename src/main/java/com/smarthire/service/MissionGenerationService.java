package com.smarthire.service;

import com.smarthire.entity.CareerMission;
import com.smarthire.entity.Notification;
import com.smarthire.entity.StudentProfile;
import com.smarthire.entity.StudentSkill;
import com.smarthire.repository.CareerMissionRepository;
import com.smarthire.repository.NotificationRepository;
import com.smarthire.repository.StudentProfileRepository;
import com.smarthire.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MissionGenerationService {

    private final CareerMissionRepository missionRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final NotificationRepository notificationRepository;
    private final CareerReadinessService careerReadinessService;

    public MissionGenerationService(
            CareerMissionRepository missionRepository,
            StudentProfileRepository studentProfileRepository,
            StudentSkillRepository studentSkillRepository,
            NotificationRepository notificationRepository,
            CareerReadinessService careerReadinessService) {
        this.missionRepository = missionRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.notificationRepository = notificationRepository;
        this.careerReadinessService = careerReadinessService;
    }

    @Transactional
    public List<CareerMission> getDailyMissions(Long studentId) {
        List<CareerMission> existing = missionRepository.findByStudentIdOrderByDateAssignedDesc(studentId);
        if (!existing.isEmpty()) {
            return existing;
        }
        return generateDefaultDailyMissions(studentId);
    }

    @Transactional
    public List<CareerMission> generateDefaultDailyMissions(Long studentId) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<CareerMission> missions = List.of(
                new CareerMission(student, "Build a Spring Boot REST API with JWT Auth",
                        "Implement an authenticated endpoint with role-based authorization filter and push code to your repository.",
                        "Backend Architecture", 25, 4, "Spring Boot", 5),
                new CareerMission(student, "Dockerize a Multi-Container Application",
                        "Write an optimized multi-stage Dockerfile and docker-compose configuration for backend and database.",
                        "DevOps & Cloud", 30, 5, "Docker", 8),
                new CareerMission(student, "Implement LRU Cache in Java",
                        "Write a thread-safe Least Recently Used cache using LinkedHashMap or custom doubly linked list with O(1) ops.",
                        "Data Structures", 20, 3, "DSA", 5),
                new CareerMission(student, "Simulate AI Mock Interview Question on Microservices",
                        "Explain service discovery, API gateway patterns, and distributed tracing in 90 seconds.",
                        "Interview Mastery", 15, 3, "System Design", 4));

        return missionRepository.saveAll(missions);
    }

    @Transactional
    public CareerMission completeMission(Long missionId) {
        CareerMission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found"));

        if (Boolean.TRUE.equals(mission.getCompleted())) {
            return mission; // already completed
        }

        mission.setCompleted(true);
        mission.setCompletedAt(LocalDateTime.now());
        missionRepository.save(mission);

        StudentProfile student = mission.getStudent();

        // 1. Boost readiness score
        int oldScore = student.getCareerReadinessScore() != null ? student.getCareerReadinessScore() : 80;
        int newScore = Math.min(99, oldScore + mission.getPoints());
        student.setCareerReadinessScore(newScore);

        // 2. Boost target skill evidence
        if (mission.getTargetSkill() != null) {
            var skillOpt = studentSkillRepository.findByStudentIdAndSkillNameIgnoreCase(student.getId(),
                    mission.getTargetSkill());
            if (skillOpt.isPresent()) {
                StudentSkill ss = skillOpt.get();
                int currentEvidence = ss.getEvidenceStrength() != null ? ss.getEvidenceStrength() : 65;
                ss.setProjectEvidenceScore(
                        Math.min(98, (ss.getProjectEvidenceScore() != null ? ss.getProjectEvidenceScore() : 65)
                                + mission.getEvidenceDelta()));
                ss.calculateEvidenceStrength();
                studentSkillRepository.save(ss);
            }
        }
        studentProfileRepository.save(student);

        // 3. Recalculate readiness
        careerReadinessService.calculateReadiness(student.getId());

        // 4. Create congratulations notification
        Notification notification = new Notification(
                student.getUser(),
                "Mission Complete ✓ " + mission.getTitle(),
                "You earned +" + mission.getPoints() + " Readiness Points! " + mission.getTargetSkill()
                        + " evidence improved by " + mission.getEvidenceDelta() + "%.",
                "SUCCESS");
        notificationRepository.save(notification);

        return mission;
    }
}
