package com.smarthire.controller;

import com.smarthire.entity.*;
import com.smarthire.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/passport")
public class CareerPassportController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CareerPassportRepository passportRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final ProjectRepository projectRepository;
    private final CertificateRepository certificateRepository;

    public CareerPassportController(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            CareerPassportRepository passportRepository,
            StudentSkillRepository studentSkillRepository,
            ProjectRepository projectRepository,
            CertificateRepository certificateRepository) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.passportRepository = passportRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.projectRepository = projectRepository;
        this.certificateRepository = certificateRepository;
    }

    private StudentProfile getStudent(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyPassport(Authentication auth) {
        StudentProfile student = getStudent(auth);
        CareerPassport passport = passportRepository.findByStudentId(student.getId())
                .orElseGet(() -> {
                    String token = "SHX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    CareerPassport p = new CareerPassport(student, token);
                    p.setQrCodeData("https://smarthire.ai/passport/" + token);
                    p.setVerifiedBadgesJson("[\"VERIFIED_FULLSTACK\", \"ATS_84\", \"PROVEN_SPRING_BOOT\", \"AUTHENTICATED_CODE\"]");
                    return passportRepository.save(p);
                });

        return ResponseEntity.ok(buildPassportPayload(student, passport));
    }

    @GetMapping("/public/{passportId}")
    public ResponseEntity<Map<String, Object>> getPublicPassport(@PathVariable String passportId) {
        CareerPassport passport = passportRepository.findByPassportId(passportId)
                .orElseThrow(() -> new RuntimeException("Passport not found"));
        StudentProfile student = passport.getStudent();
        return ResponseEntity.ok(buildPassportPayload(student, passport));
    }

    private Map<String, Object> buildPassportPayload(StudentProfile student, CareerPassport passport) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("passportId", passport.getPassportId());
        payload.put("fullName", student.getUser().getFullName());
        payload.put("targetRole", student.getTargetRole());
        payload.put("university", student.getUniversity());
        payload.put("batchYear", student.getBatchYear());
        payload.put("careerReadiness", student.getCareerReadinessScore());
        payload.put("technicalSkillsScore", student.getTechnicalSkillsScore());
        payload.put("projectEvidenceScore", student.getProjectEvidenceScore());
        payload.put("interviewReadinessScore", student.getInterviewReadinessScore());
        payload.put("githubUsername", student.getGithubUsername());
        payload.put("qrCodeData", passport.getQrCodeData());
        payload.put("issuedAt", passport.getIssuedAt());
        payload.put("lastVerifiedAt", passport.getLastVerifiedAt());

        payload.put("skills", studentSkillRepository.findByStudentId(student.getId()));
        payload.put("projects", projectRepository.findByStudentId(student.getId()));
        payload.put("certificates", certificateRepository.findByStudentId(student.getId()));

        return payload;
    }
}
