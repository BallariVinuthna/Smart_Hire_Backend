package com.smarthire.service;

import com.smarthire.dto.ApplicationDtos.PreApplyCheckResponse;
import com.smarthire.entity.*;
import com.smarthire.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class JobMatchingService {

    private final JobRepository jobRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final JobMatchRepository jobMatchRepository;
    private final ApplicationRepository applicationRepository;

    public JobMatchingService(
            JobRepository jobRepository,
            StudentProfileRepository studentProfileRepository,
            StudentSkillRepository studentSkillRepository,
            JobMatchRepository jobMatchRepository,
            ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.jobMatchRepository = jobMatchRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public JobMatch calculateJobMatch(Long studentId, Long jobId) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentId(studentId);
        Set<String> userSkills = new HashSet<>();
        for (StudentSkill ss : studentSkills) {
            userSkills.add(ss.getSkill().getName().trim().toLowerCase());
        }

        String[] requiredSkills = job.getSkillsRequired() != null ? job.getSkillsRequired().split(",") : new String[0];
        List<String> matched = new ArrayList<>();
        List<String> gaps = new ArrayList<>();

        for (String req : requiredSkills) {
            String trimmed = req.trim();
            if (userSkills.contains(trimmed.toLowerCase())) {
                matched.add(trimmed);
            } else {
                gaps.add(trimmed);
            }
        }

        int total = requiredSkills.length;
        int skillMatchScore = total > 0 ? (int) Math.round(((double) matched.size() / total) * 100) : 85;
        int readiness = student.getCareerReadinessScore() != null ? student.getCareerReadinessScore() : 84;

        int overallMatch = (int) Math.round((skillMatchScore * 0.6) + (readiness * 0.4));

        String whyText = "✓ Matches on: " + String.join(", ", matched) + 
                (gaps.isEmpty() ? ". Ready for direct application." : ". Potential gap on: " + String.join(", ", gaps) + ".");

        JobMatch match = jobMatchRepository.findByStudentIdAndJobId(studentId, jobId)
                .orElse(new JobMatch());

        match.setStudent(student);
        match.setJob(job);
        match.setOverallMatchScore(overallMatch);
        match.setSkillMatchScore(skillMatchScore);
        match.setReadinessScore(readiness);
        match.setMatchedSkills(String.join(", ", matched));
        match.setGapSkills(String.join(", ", gaps));
        match.setWhyText(whyText);

        return jobMatchRepository.save(match);
    }

    public PreApplyCheckResponse preApplyCheck(Long studentId, Long jobId) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        JobMatch match = calculateJobMatch(studentId, jobId);

        PreApplyCheckResponse response = new PreApplyCheckResponse();
        response.setJobId(job.getId());
        response.setJobTitle(job.getTitle());
        response.setCompanyName(job.getCompany().getName());
        response.setReadinessScore(student.getCareerReadinessScore() != null ? student.getCareerReadinessScore() : 84);
        response.setJobMatchScore(match.getOverallMatchScore());
        response.setResumeMatchScore(Math.min(95, match.getOverallMatchScore() - 3));
        response.setInterviewPrepScore(student.getInterviewReadinessScore() != null ? student.getInterviewReadinessScore() : 76);

        boolean isStrong = match.getOverallMatchScore() >= 80;
        response.setIsStrongMatch(isStrong);
        response.setMatchVerdict(isStrong ? "You're a strong match." : "Moderate match with target role.");

        List<String> actions = new ArrayList<>();
        actions.add("Use Java Developer Resume (ATS Score: 84)");
        actions.add("Review Spring Security & JWT session token concepts");
        actions.add("Practice 5 backend architectural questions in the AI Interview simulator");
        response.setRecommendedActions(actions);

        return response;
    }

    @Transactional
    public Application applyForJob(Long studentId, Long jobId, String coverNote) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        var existing = applicationRepository.findByStudentIdAndJobId(studentId, jobId);
        if (existing.isPresent()) {
            return existing.get();
        }

        JobMatch match = calculateJobMatch(studentId, jobId);

        Application application = new Application();
        application.setStudent(student);
        application.setJob(job);
        application.setStatus("APPLIED");
        application.setReadinessSnapshot(student.getCareerReadinessScore() != null ? student.getCareerReadinessScore() : 84);
        application.setJobMatchSnapshot(match.getOverallMatchScore());
        application.setCoverNote(coverNote != null ? coverNote : "Applicant applying via SmartHire X Career Intelligence verification.");

        return applicationRepository.save(application);
    }
}
