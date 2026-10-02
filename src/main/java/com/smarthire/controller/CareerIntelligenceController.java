package com.smarthire.controller;

import com.smarthire.dto.CareerIntelligenceDtos.*;
import com.smarthire.entity.*;
import com.smarthire.repository.*;
import com.smarthire.service.CareerReadinessService;
import com.smarthire.service.JobTwinService;
import com.smarthire.service.WhatIfSimulatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/intelligence")
public class CareerIntelligenceController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final SkillEvidenceRepository skillEvidenceRepository;
    private final ProjectRepository projectRepository;
    private final ExperienceRepository experienceRepository;
    private final CareerReadinessRepository careerReadinessRepository;
    private final CareerReadinessService careerReadinessService;
    private final JobTwinService jobTwinService;
    private final WhatIfSimulatorService whatIfSimulatorService;

    public CareerIntelligenceController(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            StudentSkillRepository studentSkillRepository,
            SkillEvidenceRepository skillEvidenceRepository,
            ProjectRepository projectRepository,
            ExperienceRepository experienceRepository,
            CareerReadinessRepository careerReadinessRepository,
            CareerReadinessService careerReadinessService,
            JobTwinService jobTwinService,
            WhatIfSimulatorService whatIfSimulatorService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.skillEvidenceRepository = skillEvidenceRepository;
        this.projectRepository = projectRepository;
        this.experienceRepository = experienceRepository;
        this.careerReadinessRepository = careerReadinessRepository;
        this.careerReadinessService = careerReadinessService;
        this.jobTwinService = jobTwinService;
        this.whatIfSimulatorService = whatIfSimulatorService;
    }

    private StudentProfile getStudent(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    @GetMapping("/career-dna")
    public ResponseEntity<CareerDnaResponse> getCareerDna(Authentication auth) {
        StudentProfile student = getStudent(auth);

        CareerDnaResponse dna = new CareerDnaResponse();
        dna.setTargetRole(student.getTargetRole() != null ? student.getTargetRole() : "Java Full Stack Engineer");
        dna.setOverallReadiness(student.getCareerReadinessScore() != null ? student.getCareerReadinessScore() : 84);

        List<StudentSkill> skills = studentSkillRepository.findByStudentId(student.getId());
        List<SkillItem> skillItems = new ArrayList<>();
        for (StudentSkill s : skills) {
            skillItems.add(new SkillItem(
                    s.getSkill().getName(),
                    s.getSkill().getCategory(),
                    s.getClaimLevel(),
                    s.getAssessmentScore(),
                    s.getProjectEvidenceScore(),
                    s.getGitHubEvidenceScore(),
                    s.getInterviewScore(),
                    s.getEvidenceStrength(),
                    s.getVerified()
            ));
        }
        dna.setSkills(skillItems);

        List<Project> projects = projectRepository.findByStudentId(student.getId());
        List<ProjectItem> projectItems = new ArrayList<>();
        for (Project p : projects) {
            projectItems.add(new ProjectItem(p.getId(), p.getTitle(), p.getDescription(), p.getTechnologies(), p.getEvidenceLevel(), p.getVerified()));
        }
        dna.setProjects(projectItems);

        List<SkillEvidence> evidences = skillEvidenceRepository.findByStudentSkillStudentId(student.getId());
        List<EvidenceItem> evidenceItems = new ArrayList<>();
        for (SkillEvidence e : evidences) {
            evidenceItems.add(new EvidenceItem(e.getTitle(), e.getSource(), e.getStudentSkill().getSkill().getName(), e.getConfidenceScore()));
        }
        dna.setEvidences(evidenceItems);

        List<Experience> experiences = experienceRepository.findByStudentId(student.getId());
        List<ExperienceItem> experienceItems = new ArrayList<>();
        for (Experience exp : experiences) {
            experienceItems.add(new ExperienceItem(exp.getTitle(), exp.getCompany(), "2024 - 2025", exp.getDescription()));
        }
        dna.setExperiences(experienceItems);

        return ResponseEntity.ok(dna);
    }

    @GetMapping("/job-twin")
    public ResponseEntity<JobTwinComparisonResponse> getJobTwinComparison(
            Authentication auth,
            @RequestParam(required = false) String targetRole) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(jobTwinService.compareStudentWithJobTwin(student.getId(), targetRole));
    }

    @GetMapping("/readiness")
    public ResponseEntity<CareerReadiness> getReadiness(Authentication auth) {
        StudentProfile student = getStudent(auth);
        CareerReadiness readiness = careerReadinessRepository.findFirstByStudentIdOrderByEvaluatedAtDesc(student.getId())
                .orElseGet(() -> careerReadinessService.calculateReadiness(student.getId()));
        return ResponseEntity.ok(readiness);
    }

    @PostMapping("/readiness/recalculate")
    public ResponseEntity<CareerReadiness> recalculateReadiness(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(careerReadinessService.calculateReadiness(student.getId()));
    }

    @PostMapping("/what-if")
    public ResponseEntity<WhatIfResponse> simulateWhatIf(Authentication auth, @RequestBody WhatIfRequest request) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(whatIfSimulatorService.simulateSkillImpact(student.getId(), request.getAddedSkills()));
    }

    @GetMapping("/proof-of-skill")
    public ResponseEntity<List<StudentSkill>> getProofOfSkill(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(studentSkillRepository.findByStudentId(student.getId()));
    }
}
