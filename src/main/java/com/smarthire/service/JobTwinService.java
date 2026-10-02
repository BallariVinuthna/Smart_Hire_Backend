package com.smarthire.service;

import com.smarthire.dto.CareerIntelligenceDtos.JobTwinComparisonResponse;
import com.smarthire.entity.JobTwin;
import com.smarthire.entity.StudentProfile;
import com.smarthire.entity.StudentSkill;
import com.smarthire.repository.JobTwinRepository;
import com.smarthire.repository.StudentProfileRepository;
import com.smarthire.repository.StudentSkillRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobTwinService {

    private final JobTwinRepository jobTwinRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;

    public JobTwinService(
            JobTwinRepository jobTwinRepository,
            StudentProfileRepository studentProfileRepository,
            StudentSkillRepository studentSkillRepository) {
        this.jobTwinRepository = jobTwinRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
    }

    public JobTwinComparisonResponse compareStudentWithJobTwin(Long studentId, String targetRole) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        final String effectiveRole = (targetRole == null || targetRole.isBlank())
                ? (student.getTargetRole() != null ? student.getTargetRole() : "Java Full Stack Engineer")
                : targetRole;

        JobTwin twin = jobTwinRepository.findByTargetRoleIgnoreCase(effectiveRole)
                .orElseGet(() -> getDefaultTwin(effectiveRole));

        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentId(studentId);
        Map<String, Integer> userSkillMap = new HashMap<>();
        for (StudentSkill ss : studentSkills) {
            userSkillMap.put(ss.getSkill().getName(), Math.max(ss.getEvidenceStrength(), ss.getClaimLevel()));
        }

        Map<String, Integer> demandMap = parseRoleDemand(twin.getCoreSkillsWeights());
        Map<String, Integer> userProfileMap = new LinkedHashMap<>();
        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        int totalWeight = 0;
        int matchedWeight = 0;

        for (Map.Entry<String, Integer> entry : demandMap.entrySet()) {
            String skill = entry.getKey();
            int required = entry.getValue();
            int userScore = userSkillMap.getOrDefault(skill, 40); // default baseline if not present
            userProfileMap.put(skill, userScore);

            totalWeight += required;
            matchedWeight += Math.min(required, userScore);

            if (userScore >= required - 8) {
                strengths.add(skill + " (" + userScore + "% vs " + required + "% required)");
            } else {
                gaps.add(skill + " (Gap: " + (required - userScore) + "%)");
            }
        }

        int overallMatch = totalWeight > 0 ? (int) Math.round(((double) matchedWeight / totalWeight) * 100) : 85;

        JobTwinComparisonResponse response = new JobTwinComparisonResponse();
        response.setTargetRole(twin.getTargetRole());
        response.setRoleSummary(twin.getRoleSummary());
        response.setOverallMatch(overallMatch);
        response.setRoleDemand(demandMap);
        response.setUserProfile(userProfileMap);
        response.setStrengths(strengths);
        response.setGaps(gaps);
        response.setRecommendation(overallMatch >= 85 
                ? "You meet the core requirements for " + twin.getTargetRole() + ". Complete Docker and System Design projects to reach 95%+ match."
                : "Focus on strengthening your " + (gaps.isEmpty() ? "cloud skills" : gaps.get(0)) + " to improve your competitive match.");

        return response;
    }

    private Map<String, Integer> parseRoleDemand(String weightsJson) {
        Map<String, Integer> map = new LinkedHashMap<>();
        // Fallback default demand if empty
        if (weightsJson == null || weightsJson.isBlank()) {
            map.put("Java", 95);
            map.put("Spring Boot", 90);
            map.put("React", 82);
            map.put("SQL", 85);
            map.put("Docker", 65);
            map.put("System Design", 60);
            return map;
        }

        // Simple JSON parser for "key":value
        String cleaned = weightsJson.replace("{", "").replace("}", "").replace("\"", "");
        for (String pair : cleaned.split(",")) {
            String[] parts = pair.split(":");
            if (parts.length == 2) {
                try {
                    map.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
                } catch (NumberFormatException ignored) {}
            }
        }
        if (map.isEmpty()) {
            map.put("Java", 95);
            map.put("Spring Boot", 90);
            map.put("React", 82);
            map.put("SQL", 85);
            map.put("Docker", 65);
            map.put("System Design", 60);
        }
        return map;
    }

    private JobTwin getDefaultTwin(String role) {
        JobTwin twin = new JobTwin();
        twin.setTargetRole(role);
        twin.setRoleSummary("High-demand industry benchmark for " + role + " demanding verified full-cycle delivery and system resilience.");
        twin.setCoreSkillsWeights("{\"Java\":95,\"Spring Boot\":90,\"React\":82,\"SQL\":85,\"Docker\":65,\"System Design\":60}");
        twin.setExperienceBenchmark("1-3 years or equivalent capstone project evidence");
        twin.setMarketDemandScore(94);
        return twin;
    }
}
