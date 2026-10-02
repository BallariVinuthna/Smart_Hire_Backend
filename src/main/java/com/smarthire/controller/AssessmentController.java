package com.smarthire.controller;

import com.smarthire.dto.AssessmentDtos.*;
import com.smarthire.entity.*;
import com.smarthire.repository.*;
import com.smarthire.service.AdaptiveAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final AssessmentRepository assessmentRepository;
    private final AssessmentQuestionRepository questionRepository;
    private final AssessmentAttemptRepository attemptRepository;
    private final AdaptiveAssessmentService assessmentService;

    public AssessmentController(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            AssessmentRepository assessmentRepository,
            AssessmentQuestionRepository questionRepository,
            AssessmentAttemptRepository attemptRepository,
            AdaptiveAssessmentService assessmentService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.assessmentService = assessmentService;
    }

    private StudentProfile getStudent(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    @GetMapping
    public ResponseEntity<List<Assessment>> getAllAssessments() {
        return ResponseEntity.ok(assessmentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Assessment> getAssessment(@PathVariable Long id) {
        return ResponseEntity.ok(assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment not found")));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<AssessmentQuestion>> getQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(questionRepository.findByAssessmentIdOrderByQuestionOrderAsc(id));
    }

    @PostMapping("/submit")
    public ResponseEntity<AssessmentResultResponse> submitAssessment(
            Authentication auth,
            @RequestBody AssessmentSubmitRequest request) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(assessmentService.evaluateAssessment(student.getId(), request));
    }

    @GetMapping("/attempts")
    public ResponseEntity<List<AssessmentAttempt>> getAttempts(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(attemptRepository.findByStudentIdOrderByCompletedAtDesc(student.getId()));
    }
}
