package com.smarthire.service;

import com.smarthire.dto.AssessmentDtos.AssessmentResultResponse;
import com.smarthire.dto.AssessmentDtos.AssessmentSubmitRequest;
import com.smarthire.entity.*;
import com.smarthire.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AdaptiveAssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final AssessmentQuestionRepository questionRepository;
    private final AssessmentAttemptRepository attemptRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final CareerReadinessService careerReadinessService;

    public AdaptiveAssessmentService(
            AssessmentRepository assessmentRepository,
            AssessmentQuestionRepository questionRepository,
            AssessmentAttemptRepository attemptRepository,
            StudentProfileRepository studentProfileRepository,
            StudentSkillRepository studentSkillRepository,
            CareerReadinessService careerReadinessService) {
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.careerReadinessService = careerReadinessService;
    }

    @Transactional
    public AssessmentResultResponse evaluateAssessment(Long studentId, AssessmentSubmitRequest request) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new RuntimeException("Assessment not found"));

        List<AssessmentQuestion> questions = questionRepository.findByAssessmentIdOrderByQuestionOrderAsc(assessment.getId());
        int totalQuestions = questions.size();
        int correctAnswers = 0;

        Map<Long, String> answers = request.getAnswers() != null ? request.getAnswers() : Map.of();

        for (AssessmentQuestion q : questions) {
            String selected = answers.get(q.getId());
            if (selected != null && selected.trim().equalsIgnoreCase(q.getCorrectOption().trim())) {
                correctAnswers++;
            }
        }

        int score = totalQuestions > 0 ? (int) Math.round(((double) correctAnswers / totalQuestions) * 100) : 0;

        String foundationLevel;
        if (score >= 85) foundationLevel = "Expert Mastery";
        else if (score >= 75) foundationLevel = "Strong Foundation";
        else if (score >= 60) foundationLevel = "Competent";
        else foundationLevel = "Developing";

        AssessmentAttempt attempt = new AssessmentAttempt();
        attempt.setStudent(student);
        attempt.setAssessment(assessment);
        attempt.setScore(score);
        attempt.setTotalQuestions(totalQuestions);
        attempt.setCorrectAnswers(correctAnswers);
        attempt.setStatus("COMPLETED");
        attempt.setFoundationLevel(foundationLevel);
        attempt = attemptRepository.save(attempt);

        // Update student skill evidence if corresponding skill exists
        if (assessment.getSkillCategory() != null) {
            var studentSkillOpt = studentSkillRepository.findByStudentIdAndSkillNameIgnoreCase(studentId, assessment.getSkillCategory());
            if (studentSkillOpt.isPresent()) {
                StudentSkill ss = studentSkillOpt.get();
                ss.setAssessmentScore(score);
                studentSkillRepository.save(ss);
            }
        }

        // Trigger readiness recalculation
        careerReadinessService.calculateReadiness(studentId);

        AssessmentResultResponse response = new AssessmentResultResponse();
        response.setAttemptId(attempt.getId());
        response.setAssessmentTitle(assessment.getTitle());
        response.setScore(score);
        response.setTotalQuestions(totalQuestions);
        response.setCorrectAnswers(correctAnswers);
        response.setFoundationLevel(foundationLevel);
        String feedback;
        if (score >= 85) {
            feedback = "Excellent conceptual depth! Your verified assessment score has updated your proof-of-skill matrix.";
        } else if (score >= 70) {
            feedback = "Good performance! You've demonstrated solid understanding. Review the missed questions to close remaining gaps.";
        } else if (score >= 50) {
            feedback = "Partial mastery detected. Focus on the concepts you missed and retake to strengthen your skill proof.";
        } else {
            feedback = "Keep practicing! Review the foundational concepts for this topic and attempt again to raise your score.";
        }
        response.setFeedback(feedback);

        return response;
    }
}
