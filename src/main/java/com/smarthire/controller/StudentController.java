package com.smarthire.controller;

import com.smarthire.entity.*;
import com.smarthire.repository.*;
import com.smarthire.service.CareerReadinessService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;
    private final CertificateRepository certificateRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final NotificationRepository notificationRepository;
    private final CareerReadinessService careerReadinessService;

    public StudentController(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            StudentSkillRepository studentSkillRepository,
            SkillRepository skillRepository,
            ProjectRepository projectRepository,
            CertificateRepository certificateRepository,
            ExperienceRepository experienceRepository,
            EducationRepository educationRepository,
            NotificationRepository notificationRepository,
            CareerReadinessService careerReadinessService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.skillRepository = skillRepository;
        this.projectRepository = projectRepository;
        this.certificateRepository = certificateRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.notificationRepository = notificationRepository;
        this.careerReadinessService = careerReadinessService;
    }

    private StudentProfile getStudent(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    @GetMapping("/profile")
    public ResponseEntity<StudentProfile> getProfile(Authentication auth) {
        return ResponseEntity.ok(getStudent(auth));
    }

    @PutMapping("/profile")
    public ResponseEntity<StudentProfile> updateProfile(Authentication auth, @RequestBody StudentProfile updated) {
        StudentProfile existing = getStudent(auth);
        if (updated.getHeadline() != null) existing.setHeadline(updated.getHeadline());
        if (updated.getBio() != null) existing.setBio(updated.getBio());
        if (updated.getTargetRole() != null) existing.setTargetRole(updated.getTargetRole());
        if (updated.getGithubUsername() != null) existing.setGithubUsername(updated.getGithubUsername());
        if (updated.getLinkedinUrl() != null) existing.setLinkedinUrl(updated.getLinkedinUrl());
        if (updated.getPortfolioUrl() != null) existing.setPortfolioUrl(updated.getPortfolioUrl());
        if (updated.getCgpa() != null) existing.setCgpa(updated.getCgpa());
        existing = studentProfileRepository.save(existing);
        return ResponseEntity.ok(existing);
    }

    @GetMapping("/skills")
    public ResponseEntity<List<StudentSkill>> getSkills(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(studentSkillRepository.findByStudentId(student.getId()));
    }

    @PostMapping("/skills")
    public ResponseEntity<StudentSkill> addOrUpdateSkill(
            Authentication auth,
            @RequestParam String skillName,
            @RequestParam(defaultValue = "80") Integer claimLevel) {
        StudentProfile student = getStudent(auth);
        Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                .orElseGet(() -> skillRepository.save(new Skill(skillName, "Technical", 85)));

        StudentSkill studentSkill = studentSkillRepository.findByStudentIdAndSkillId(student.getId(), skill.getId())
                .orElse(new StudentSkill(student, skill, claimLevel));

        studentSkill.setClaimLevel(claimLevel);
        studentSkill.calculateEvidenceStrength();
        studentSkill = studentSkillRepository.save(studentSkill);

        careerReadinessService.calculateReadiness(student.getId());
        return ResponseEntity.ok(studentSkill);
    }

    @GetMapping("/projects")
    public ResponseEntity<List<Project>> getProjects(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(projectRepository.findByStudentId(student.getId()));
    }

    @PostMapping("/projects")
    public ResponseEntity<Project> addProject(Authentication auth, @RequestBody Project project) {
        StudentProfile student = getStudent(auth);
        project.setStudent(student);
        if (project.getEvidenceLevel() == null) project.setEvidenceLevel("Medium");
        if (project.getVerified() == null) project.setVerified(true);
        Project saved = projectRepository.save(project);
        careerReadinessService.calculateReadiness(student.getId());
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<?> deleteProject(Authentication auth, @PathVariable Long id) {
        StudentProfile student = getStudent(auth);
        projectRepository.findById(id).ifPresent(p -> {
            if (p.getStudent().getId().equals(student.getId())) {
                projectRepository.delete(p);
                careerReadinessService.calculateReadiness(student.getId());
            }
        });
        return ResponseEntity.ok().build();
    }

    @GetMapping("/certificates")
    public ResponseEntity<List<Certificate>> getCertificates(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(certificateRepository.findByStudentId(student.getId()));
    }

    @PostMapping("/certificates")
    public ResponseEntity<Certificate> addCertificate(Authentication auth, @RequestBody Certificate cert) {
        StudentProfile student = getStudent(auth);
        cert.setStudent(student);
        Certificate saved = certificateRepository.save(cert);
        careerReadinessService.calculateReadiness(student.getId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/experiences")
    public ResponseEntity<List<Experience>> getExperiences(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(experienceRepository.findByStudentId(student.getId()));
    }

    @PostMapping("/experiences")
    public ResponseEntity<Experience> addExperience(Authentication auth, @RequestBody Experience exp) {
        StudentProfile student = getStudent(auth);
        exp.setStudent(student);
        Experience saved = experienceRepository.save(exp);
        careerReadinessService.calculateReadiness(student.getId());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/educations")
    public ResponseEntity<List<Education>> getEducations(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(educationRepository.findByStudentId(student.getId()));
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotifications(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()));
    }

    @PatchMapping("/notifications/{id}/read")
    public ResponseEntity<?> markNotificationRead(@PathVariable Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
        return ResponseEntity.ok().build();
    }
}
