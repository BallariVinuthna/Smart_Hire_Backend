package com.smarthire.controller;

import com.smarthire.dto.PlacementDtos.RecruiterTalentMetricsResponse;
import com.smarthire.entity.*;
import com.smarthire.repository.*;
import com.smarthire.service.PlacementIntelligenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruiter")
public class RecruiterController {

    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final PlacementIntelligenceService placementIntelligenceService;

    public RecruiterController(
            UserRepository userRepository,
            RecruiterProfileRepository recruiterProfileRepository,
            JobRepository jobRepository,
            ApplicationRepository applicationRepository,
            PlacementIntelligenceService placementIntelligenceService) {
        this.userRepository = userRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.placementIntelligenceService = placementIntelligenceService;
    }

    private RecruiterProfile getRecruiter(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return recruiterProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));
    }

    @GetMapping("/metrics")
    public ResponseEntity<RecruiterTalentMetricsResponse> getMetrics(Authentication auth) {
        RecruiterProfile recruiter = null;
        try { recruiter = getRecruiter(auth); } catch (Exception ignored) {}
        Long companyId = recruiter != null && recruiter.getCompany() != null ? recruiter.getCompany().getId() : 1L;
        return ResponseEntity.ok(placementIntelligenceService.getRecruiterMetrics(companyId));
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getMyJobs(Authentication auth) {
        RecruiterProfile recruiter = getRecruiter(auth);
        return ResponseEntity.ok(jobRepository.findByCompanyId(recruiter.getCompany().getId()));
    }

    @PostMapping("/jobs")
    public ResponseEntity<Job> createJob(Authentication auth, @RequestBody Job job) {
        RecruiterProfile recruiter = getRecruiter(auth);
        job.setCompany(recruiter.getCompany());
        job.setStatus("ACTIVE");
        return ResponseEntity.ok(jobRepository.save(job));
    }

    @PatchMapping("/applications/{id}/status")
    public ResponseEntity<Application> updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus(status.toUpperCase());
        return ResponseEntity.ok(applicationRepository.save(application));
    }
}
