package com.smarthire.service;

import com.smarthire.entity.CareerReadiness;
import com.smarthire.entity.StudentProfile;
import com.smarthire.entity.StudentSkill;
import com.smarthire.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CareerReadinessService {

    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final ProjectRepository projectRepository;
    private final InterviewRepository interviewRepository;
    private final ExperienceRepository experienceRepository;
    private final CareerReadinessRepository careerReadinessRepository;

    public CareerReadinessService(
            StudentProfileRepository studentProfileRepository,
            StudentSkillRepository studentSkillRepository,
            ProjectRepository projectRepository,
            InterviewRepository interviewRepository,
            ExperienceRepository experienceRepository,
            CareerReadinessRepository careerReadinessRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.projectRepository = projectRepository;
        this.interviewRepository = interviewRepository;
        this.experienceRepository = experienceRepository;
        this.careerReadinessRepository = careerReadinessRepository;
    }

    @Transactional
    public CareerReadiness calculateReadiness(Long studentId) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<StudentSkill> skills = studentSkillRepository.findByStudentId(studentId);

        // 1. Skills Score (average of claim and verified strengths)
        int skillsScore = 75;
        if (!skills.isEmpty()) {
            int total = 0;
            for (StudentSkill s : skills) {
                total += (s.getClaimLevel() + s.getEvidenceStrength()) / 2;
            }
            skillsScore = total / skills.size();
        }

        // 2. Evidence Score (average of verified evidence strengths)
        int evidenceScore = 70;
        if (!skills.isEmpty()) {
            int total = 0;
            for (StudentSkill s : skills) {
                total += s.getEvidenceStrength();
            }
            evidenceScore = total / skills.size();
        }

        // 3. Projects Score
        int projectsScore = Math.min(100, Math.max(60, projectRepository.findByStudentId(studentId).size() * 30));

        // 4. Interview Score
        int interviewScore = interviewRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .findFirst()
                .map(i -> i.getOverallScore())
                .orElse(76);

        // 5. Experience Score
        int experienceScore = Math.min(100, Math.max(50, experienceRepository.findByStudentId(studentId).size() * 25 + 50));

        // Overall Weighted Career Readiness Score
        // Formula: Skills (25%) + Evidence (25%) + Projects (20%) + Interview (20%) + Experience (10%)
        int overallScore = (int) Math.round(
                skillsScore * 0.25 +
                evidenceScore * 0.25 +
                projectsScore * 0.20 +
                interviewScore * 0.20 +
                experienceScore * 0.10
        );

        student.setCareerReadinessScore(overallScore);
        student.setTechnicalSkillsScore(skillsScore);
        student.setProjectEvidenceScore(evidenceScore);
        student.setInterviewReadinessScore(interviewScore);
        student.setExperienceScore(experienceScore);
        studentProfileRepository.save(student);

        CareerReadiness readiness = new CareerReadiness(
                student,
                overallScore,
                skillsScore,
                evidenceScore,
                projectsScore,
                interviewScore,
                experienceScore
        );
        readiness.setSummaryText(buildReadinessSummary(overallScore));
        return careerReadinessRepository.save(readiness);
    }

    private String buildReadinessSummary(int score) {
        if (score >= 85) {
            return "Exceptional job readiness. Evidence verified across technical, architectural, and interview dimensions.";
        } else if (score >= 75) {
            return "Strong candidate readiness. Your core skills and projects demonstrate job capability; continuing daily missions will push you to elite tier.";
        } else {
            return "Solid baseline. Add verifiable GitHub projects and complete adaptive assessments to elevate proof-of-skill.";
        }
    }
}
