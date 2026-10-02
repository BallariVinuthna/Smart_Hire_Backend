package com.smarthire.controller;

import com.smarthire.dto.ResumeDtos.*;
import com.smarthire.entity.Resume;
import com.smarthire.entity.ResumeVersion;
import com.smarthire.entity.StudentProfile;
import com.smarthire.entity.User;
import com.smarthire.repository.ResumeRepository;
import com.smarthire.repository.ResumeVersionRepository;
import com.smarthire.repository.StudentProfileRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.ResumeAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeController(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            ResumeRepository resumeRepository,
            ResumeVersionRepository resumeVersionRepository,
            ResumeAnalysisService resumeAnalysisService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.resumeRepository = resumeRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.resumeAnalysisService = resumeAnalysisService;
    }

    private StudentProfile getStudent(Authentication auth) {
        if (auth != null && auth.getName() != null) {
            Optional<User> userOpt = userRepository.findByEmail(auth.getName());
            if (userOpt.isPresent()) {
                Optional<StudentProfile> profileOpt = studentProfileRepository.findByUserId(userOpt.get().getId());
                if (profileOpt.isPresent()) {
                    return profileOpt.get();
                }
            }
        }
        return studentProfileRepository.findAll().stream().findFirst().orElse(null);
    }

    @GetMapping
    public ResponseEntity<List<Resume>> getMyResumes(Authentication auth) {
        StudentProfile student = getStudent(auth);
        List<Resume> list = (student != null) ? resumeRepository.findByStudentId(student.getId()) : List.of();
        if (list == null || list.isEmpty()) {
            list = resumeRepository.findAll();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resume> getResume(Authentication auth, @PathVariable Long id) {
        Resume resume = resumeRepository.findById(id)
                .orElseGet(() -> resumeRepository.findAll().stream().findFirst().orElse(null));
        if (resume == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resume);
    }

    @PostMapping
    public ResponseEntity<Resume> createResume(Authentication auth, @RequestBody ResumeRequest request) {
        StudentProfile student = getStudent(auth);
        if (student == null) {
            return ResponseEntity.badRequest().build();
        }

        Resume resume = new Resume();
        resume.setStudent(student);
        resume.setTitle(request.getTitle() != null ? request.getTitle() : "My Professional Resume");
        resume.setTemplateType(request.getTemplateType() != null ? request.getTemplateType() : "Modern");
        resume.setIsPrimary(Boolean.TRUE.equals(request.getIsPrimary()));
        resume.setContentJson(request.getContentJson());
        resume = resumeRepository.save(resume);

        ResumeVersion v1 = new ResumeVersion(resume, 1, resume.getContentJson());
        resumeVersionRepository.save(v1);

        return ResponseEntity.ok(resume);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Resume> updateResume(Authentication auth, @PathVariable Long id, @RequestBody ResumeRequest request) {
        Resume resume = resumeRepository.findById(id)
                .orElseGet(() -> resumeRepository.findAll().stream().findFirst().orElse(null));
        if (resume == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.getTitle() != null) resume.setTitle(request.getTitle());
        if (request.getTemplateType() != null) resume.setTemplateType(request.getTemplateType());
        if (request.getIsPrimary() != null) resume.setIsPrimary(request.getIsPrimary());
        if (request.getContentJson() != null) resume.setContentJson(request.getContentJson());

        resume = resumeRepository.save(resume);

        List<ResumeVersion> versions = resumeVersionRepository.findByResumeIdOrderByVersionNumberDesc(resume.getId());
        int nextVersion = versions.isEmpty() ? 1 : versions.get(0).getVersionNumber() + 1;
        ResumeVersion version = new ResumeVersion(resume, nextVersion, resume.getContentJson());
        resumeVersionRepository.save(version);

        return ResponseEntity.ok(resume);
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<AtsAnalysisResponse> analyzeResume(Authentication auth, @PathVariable Long id) {
        return ResponseEntity.ok(resumeAnalysisService.analyzeResume(id));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<AtsAnalysisResponse> uploadAndAnalyze(
            Authentication auth,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(resumeAnalysisService.analyzeUploadedFile(id, file));
    }

    @PostMapping("/upload")
    public ResponseEntity<AtsAnalysisResponse> uploadAndAnalyzeDirect(
            Authentication auth,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(resumeAnalysisService.analyzeUploadedFile(null, file));
    }
}
