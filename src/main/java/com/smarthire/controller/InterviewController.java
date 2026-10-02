package com.smarthire.controller;

import com.smarthire.dto.InterviewDtos.*;
import com.smarthire.entity.*;
import com.smarthire.repository.*;
import com.smarthire.service.InterviewAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewAIService interviewAIService;

    public InterviewController(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            InterviewRepository interviewRepository,
            InterviewQuestionRepository questionRepository,
            InterviewAIService interviewAIService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
        this.interviewAIService = interviewAIService;
    }

    private StudentProfile getStudent(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));
    }

    @PostMapping("/start")
    public ResponseEntity<Interview> startInterview(
            Authentication auth,
            @RequestBody(required = false) InterviewStartRequest request) {
        StudentProfile student = getStudent(auth);
        String targetRole = request != null ? request.getTargetRole() : "Java Full Stack Engineer";
        return ResponseEntity.ok(interviewAIService.startInterview(student.getId(), targetRole));
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<InterviewQuestion>> getInterviewQuestions(@PathVariable Long id) {
        return ResponseEntity.ok(questionRepository.findByInterviewIdOrderByQuestionOrderAsc(id));
    }

    @PostMapping("/{id}/submit-answer")
    public ResponseEntity<?> submitAnswer(
            @PathVariable Long id,
            @RequestBody InterviewSubmitAnswerRequest request) {
        return ResponseEntity.ok(interviewAIService.submitAnswer(id, request));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<InterviewEvaluationResponse> completeInterview(@PathVariable Long id) {
        return ResponseEntity.ok(interviewAIService.completeInterview(id));
    }

    @GetMapping("/history")
    public ResponseEntity<List<Interview>> getInterviewHistory(Authentication auth) {
        StudentProfile student = getStudent(auth);
        return ResponseEntity.ok(interviewRepository.findByStudentIdOrderByCreatedAtDesc(student.getId()));
    }
}
