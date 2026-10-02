package com.smarthire.service;

import com.smarthire.dto.CareerIntelligenceDtos.SkillGainItem;
import com.smarthire.dto.CareerIntelligenceDtos.WhatIfResponse;
import com.smarthire.entity.StudentProfile;
import com.smarthire.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WhatIfSimulatorService {

    private final StudentProfileRepository studentProfileRepository;

    public WhatIfSimulatorService(StudentProfileRepository studentProfileRepository) {
        this.studentProfileRepository = studentProfileRepository;
    }

    public WhatIfResponse simulateSkillImpact(Long studentId, List<String> addedSkills) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        int currentReadiness = student.getCareerReadinessScore() != null && student.getCareerReadinessScore() > 0
                ? student.getCareerReadinessScore()
                : 72;

        int totalGain = 0;
        List<SkillGainItem> gains = new ArrayList<>();

        if (addedSkills != null) {
            for (String rawSkill : addedSkills) {
                String skill = rawSkill.trim();
                int point;
                String reason;
                if (skill.equalsIgnoreCase("Spring Boot")) {
                    point = 6;
                    reason = "Directly satisfies core backend framework requirement for enterprise Java stacks.";
                } else if (skill.equalsIgnoreCase("Docker") || skill.equalsIgnoreCase("Kubernetes")) {
                    point = 5;
                    reason = "Bridges the deployment and containerization gap favored in modern cloud-native environments.";
                } else if (skill.equalsIgnoreCase("AWS") || skill.equalsIgnoreCase("Cloud")) {
                    point = 5;
                    reason = "Adds cloud infrastructure competency and production deployment proof.";
                } else if (skill.equalsIgnoreCase("System Design")) {
                    point = 6;
                    reason = "High-leverage differentiator in technical interviews for Tier-1 technology companies.";
                } else if (skill.equalsIgnoreCase("React") || skill.equalsIgnoreCase("TypeScript")) {
                    point = 4;
                    reason = "Completes full-stack capability and autonomous frontend delivery.";
                } else {
                    point = 3;
                    reason = "Expands technical versatility and domain breadth.";
                }
                totalGain += point;
                gains.add(new SkillGainItem(skill, point, reason));
            }
        }

        // Cap projected readiness realistically at 99%
        int projectedReadiness = Math.min(99, currentReadiness + totalGain);
        int effectiveDelta = projectedReadiness - currentReadiness;

        WhatIfResponse response = new WhatIfResponse();
        response.setCurrentReadiness(currentReadiness);
        response.setProjectedReadiness(projectedReadiness);
        response.setDelta(effectiveDelta);
        response.setGains(gains);
        response.setExplanation("Estimated improvement based on your target role (" +
                (student.getTargetRole() != null ? student.getTargetRole() : "Java Full Stack Engineer") +
                ") and current verified evidence profile. Results reflect potential readiness upon completing verified project proof.");

        return response;
    }
}
