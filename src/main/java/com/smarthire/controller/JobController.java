package com.smarthire.controller;

import com.smarthire.dto.ApplicationDtos.*;
import com.smarthire.entity.*;
import com.smarthire.repository.*;
import com.smarthire.service.JobMatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SavedJobRepository savedJobRepository;
    private final ApplicationRepository applicationRepository;
    private final JobMatchingService jobMatchingService;

    public JobController(
            JobRepository jobRepository,
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            SavedJobRepository savedJobRepository,
            ApplicationRepository applicationRepository,
            JobMatchingService jobMatchingService) {
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.savedJobRepository = savedJobRepository;
        this.applicationRepository = applicationRepository;
        this.jobMatchingService = jobMatchingService;
    }

    private StudentProfile getStudent(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getJobs(
            Authentication auth,
            @RequestParam(required = false) String search) {
        List<Job> jobs;
        if (search != null && !search.isBlank()) {
            jobs = jobRepository.findByTitleContainingIgnoreCaseOrSkillsRequiredContainingIgnoreCase(search, search);
        } else {
            jobs = jobRepository.findByStatus("ACTIVE");
        }

        StudentProfile student = null;
        if (auth != null && auth.isAuthenticated()) {
            try { student = getStudent(auth); } catch (Exception ignored) {}
        }

        List<Map<String, Object>> response = new ArrayList<>();
        for (Job job : jobs) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", job.getId());
            map.put("title", job.getTitle());
            map.put("company", job.getCompany().getName());
            map.put("companyLogo", job.getCompany().getLogoUrl());
            map.put("location", job.getLocation());
            map.put("workType", job.getWorkType());
            map.put("salaryRange", job.getSalaryRange());
            map.put("experienceRequired", job.getExperienceRequired());
            map.put("description", job.getDescription());
            map.put("requirements", job.getRequirements());
            map.put("skillsRequired", job.getSkillsRequired());
            map.put("minReadinessScore", job.getMinReadinessScore());

            if (student != null) {
                JobMatch match = jobMatchingService.calculateJobMatch(student.getId(), job.getId());
                map.put("matchScore", match.getOverallMatchScore());
                map.put("skillMatchScore", match.getSkillMatchScore());
                map.put("matchedSkills", match.getMatchedSkills());
                map.put("gapSkills", match.getGapSkills());
                map.put("whyText", match.getWhyText());
            } else {
                map.put("matchScore", 85);
                map.put("matchedSkills", "Java, SQL");
                map.put("gapSkills", "Docker");
                map.put("whyText", "Sign in to calculate personalized job twin match.");
            }
            response.add(map);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobDetails(@PathVariable Long id) {
        return ResponseEntity.ok(jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found")));
    }

    @GetMapping("/{id}/pre-apply-check")
    public ResponseEntity<PreApplyCheckResponse> getPreApplyCheck(Authentication auth, @PathVariable Long id) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(jobMatchingService.preApplyCheck(student.getId(), id));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<Application> applyToJob(
            Authentication auth,
            @PathVariable Long id,
            @RequestBody(required = false) ApplyJobRequest request) {
        StudentProfile student = getStudent(auth);
        String note = request != null ? request.getCoverNote() : null;
        return ResponseEntity.ok(jobMatchingService.applyForJob(student.getId(), id, note));
    }

    @GetMapping("/applications")
    public ResponseEntity<List<Application>> getMyApplications(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(applicationRepository.findByStudentIdOrderByAppliedAtDesc(student.getId()));
    }

    @GetMapping("/saved")
    public ResponseEntity<List<SavedJob>> getSavedJobs(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(savedJobRepository.findByStudentId(student.getId()));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<?> toggleSaveJob(Authentication auth, @PathVariable Long id) {
        StudentProfile student = getStudent(auth);
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        var existing = savedJobRepository.findByStudentIdAndJobId(student.getId(), id);
        if (existing.isPresent()) {
            savedJobRepository.delete(existing.get());
            return ResponseEntity.ok(Map.of("saved", false));
        } else {
            savedJobRepository.save(new SavedJob(student, job));
            return ResponseEntity.ok(Map.of("saved", true));
        }
    }
}
