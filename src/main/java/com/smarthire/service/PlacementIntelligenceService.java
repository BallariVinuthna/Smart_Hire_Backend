package com.smarthire.service;

import com.smarthire.dto.PlacementDtos.*;
import com.smarthire.entity.Application;
import com.smarthire.entity.StudentProfile;
import com.smarthire.repository.ApplicationRepository;
import com.smarthire.repository.PlacementDriveRepository;
import com.smarthire.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlacementIntelligenceService {

    private final StudentProfileRepository studentProfileRepository;
    private final ApplicationRepository applicationRepository;
    private final PlacementDriveRepository placementDriveRepository;

    public PlacementIntelligenceService(
            StudentProfileRepository studentProfileRepository,
            ApplicationRepository applicationRepository,
            PlacementDriveRepository placementDriveRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.applicationRepository = applicationRepository;
        this.placementDriveRepository = placementDriveRepository;
    }

    public BatchStatsResponse getBatchStatistics(Integer batchYear) {
        if (batchYear == null)
            batchYear = 2027;

        BatchStatsResponse response = new BatchStatsResponse();
        response.setBatchYear(batchYear);
        response.setTotalStudents(842);
        response.setResumeReady(683);
        response.setInterviewReady(491);
        response.setPlacementReady(438);

        List<SkillGapItem> gaps = List.of(
                new SkillGapItem("Spring Boot", "High Gap", 58, 352),
                new SkillGapItem("DSA & Problem Solving", "High Gap", 62, 310),
                new SkillGapItem("Cloud & Docker", "Medium", 68, 220),
                new SkillGapItem("SQL & Database Design", "Strong", 84, 85),
                new SkillGapItem("React & Modern Frontend", "Strong", 82, 95));
        response.setSkillGaps(gaps);

        List<WorkshopSuggestion> workshops = List.of(
                new WorkshopSuggestion("Spring Boot & Production REST APIs", "Enterprise Java Architecture", 352,
                        "Next 2 Weeks"),
                new WorkshopSuggestion("High-Frequency DSA Patterns & Trees/Graphs", "Algorithms & Interview Prep", 310,
                        "Month 1"),
                new WorkshopSuggestion("Containerization & Cloud Native Basics", "DevOps & Infrastructure", 220,
                        "Month 2"));
        response.setWorkshopSuggestions(workshops);

        return response;
    }

    public RecruiterTalentMetricsResponse getRecruiterMetrics(Long companyId) {
        RecruiterTalentMetricsResponse metrics = new RecruiterTalentMetricsResponse();
        metrics.setApplicantsCount(128);
        metrics.setStrongMatchesCount(42);
        metrics.setInterviewReadyCount(18);
        metrics.setSelectedCount(7);

        List<CandidateItem> candidates = List.of(
                new CandidateItem(1L, "Ballari Vinuthna", "Java Full Stack Engineer", 92, 84, 89, 82, 82,
                        "1.5 Years Project / Internship", "SHORTLISTED"),
                new CandidateItem(2L, "Alex Rivera", "Frontend Specialist", 88, 76, 85, 78, 74, "1 Year Frontend",
                        "ASSESSMENT"),
                new CandidateItem(3L, "Priya Sharma", "Cloud & Backend Engineer", 90, 81, 88, 80, 79,
                        "2 Years Academic Projects", "INTERVIEW"),
                new CandidateItem(4L, "Marcus Vance", "Java Developer", 78, 70, 74, 68, 70, "Final Year Capstone",
                        "APPLIED"),
                new CandidateItem(5L, "Ananya Rao", "Full Stack Developer", 85, 79, 82, 75, 76,
                        "Freelance & Open Source", "SELECTED"));
        metrics.setCandidates(candidates);

        return metrics;
    }
}
