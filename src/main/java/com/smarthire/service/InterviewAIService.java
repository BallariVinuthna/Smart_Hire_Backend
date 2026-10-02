package com.smarthire.service;

import com.smarthire.dto.InterviewDtos.InterviewEvaluationResponse;
import com.smarthire.dto.InterviewDtos.InterviewSubmitAnswerRequest;
import com.smarthire.dto.InterviewDtos.QuestionFeedbackItem;
import com.smarthire.entity.*;
import com.smarthire.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class InterviewAIService {

    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository questionRepository;
    private final InterviewResponseRepository responseRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CareerReadinessService careerReadinessService;

    public InterviewAIService(
            InterviewRepository interviewRepository,
            InterviewQuestionRepository questionRepository,
            InterviewResponseRepository responseRepository,
            StudentProfileRepository studentProfileRepository,
            CareerReadinessService careerReadinessService) {
        this.interviewRepository = interviewRepository;
        this.questionRepository = questionRepository;
        this.responseRepository = responseRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.careerReadinessService = careerReadinessService;
    }

    @Transactional
    public Interview startInterview(Long studentId, String targetRole) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (targetRole == null || targetRole.isBlank()) {
            targetRole = student.getTargetRole() != null ? student.getTargetRole() : "Java Full Stack Engineer";
        }

        // Abandon any stale IN_PROGRESS interviews and wipe their responses so scores
        // never carry over
        try {
            var existingOpt = interviewRepository.findFirstByStudentIdAndStatusOrderByCreatedAtDesc(studentId,
                    "IN_PROGRESS");
            if (existingOpt.isPresent()) {
                Interview stale = existingOpt.get();
                List<InterviewQuestion> staleQuestions = questionRepository
                        .findByInterviewIdOrderByQuestionOrderAsc(stale.getId());
                for (InterviewQuestion sq : staleQuestions) {
                    responseRepository.findByQuestionId(sq.getId()).ifPresent(responseRepository::delete);
                }
                stale.setStatus("ABANDONED");
                interviewRepository.save(stale);
            }
        } catch (Exception ignored) {
            // If cleanup fails, still proceed to create a fresh interview
        }

        Interview interview = new Interview();
        interview.setStudent(student);
        interview.setTitle(targetRole + " AI Mock Simulation");
        interview.setTargetRole(targetRole);
        interview.setStatus("IN_PROGRESS");
        interview = interviewRepository.save(interview);

        // Generate interview questions
        List<String[]> defaultQuestions = List.of(
                new String[] {
                        "Explain how JSON Web Tokens (JWT) work, including their structure and stateless verification.",
                        "JWT Security",
                        "JWT consists of Header, Payload, and Signature. The signature ensures tamper-proof verification without querying a session store on every request." },
                new String[] {
                        "How do you handle database concurrency and avoid race conditions in a Spring Boot application?",
                        "Concurrency",
                        "Optimistic locking using @Version, pessimistic locking with database row locks, or isolation levels like SERIALIZABLE." },
                new String[] {
                        "Describe a scenario where you would choose microservices over a monolithic architecture, and the trade-offs involved.",
                        "Microservices",
                        "Microservices decouple domains for autonomous team scaling, but introduce network latency, distributed transactions (Saga pattern), and observability complexity." },
                new String[] { "How does React's virtual DOM reconciliation algorithm optimize rendering performance?",
                        "Frontend",
                        "React compares the virtual DOM tree using heuristic diffing, updating only modified nodes in batch operations." },
                new String[] {
                        "Walk me through how you structured the authentication and database schema in your most significant project.",
                        "Project Depth",
                        "Clear separation of controller/service/repository, normalized schema with indexing, and secure token refresh mechanisms." });

        int order = 1;
        for (String[] q : defaultQuestions) {
            InterviewQuestion question = new InterviewQuestion(interview, order++, q[0], q[1], q[2]);
            questionRepository.save(question);
        }

        return interview;
    }

    @Transactional
    public InterviewResponse submitAnswer(Long interviewId, InterviewSubmitAnswerRequest request) {
        InterviewQuestion question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        String transcript = (request.getTranscript() != null) ? request.getTranscript().trim() : "";

        InterviewResponse resp = responseRepository.findByQuestionId(question.getId())
                .orElse(new InterviewResponse());

        resp.setQuestion(question);
        resp.setTranscript(transcript);
        resp.setResponseAudioUrl(request.getAudioUrl());

        // Content-based evaluation against the expected answer
        int score;
        String eval;
        String status;

        if (transcript.isEmpty()) {
            score = 0;
            eval = "Unattempted";
            status = "unattempted";
        } else {
            // Extract meaningful keywords from the model answer (skip short stop words)
            String modelAnswer = question.getIdealAnswer() != null ? question.getIdealAnswer() : "";
            String[] modelWords = modelAnswer.toLowerCase(Locale.ROOT)
                    .replaceAll("[^a-z0-9\\s]", " ")
                    .split("\\s+");

            List<String> keywords = Arrays.stream(modelWords)
                    .filter(w -> w.length() > 4) // skip short/common words
                    .distinct()
                    .toList();

            String lowerTranscript = transcript.toLowerCase(Locale.ROOT);
            long matched = keywords.stream().filter(lowerTranscript::contains).count();

            // Base score: proportional keyword coverage, 40–100 range
            // If no keywords in model answer, treat as 0 match — do NOT inflate
            double coverage = keywords.isEmpty() ? 0.0 : (double) matched / keywords.size();
            int baseScore = (int) Math.round(40 + coverage * 60);

            // Depth bonus: longer, well-structured answers get up to +5
            int depthBonus = 0;
            if (transcript.length() > 200)
                depthBonus = 5;
            else if (transcript.length() > 100)
                depthBonus = 3;
            else if (transcript.length() > 50)
                depthBonus = 1;

            score = Math.min(100, baseScore + depthBonus);

            // Build contextual feedback
            List<String> missedKeywords = keywords.stream()
                    .filter(k -> !lowerTranscript.contains(k))
                    .limit(3)
                    .toList();

            if (score >= 85) {
                eval = "Excellent answer! You covered the key concepts thoroughly and demonstrated strong technical depth.";
            } else if (score >= 70) {
                String hint = missedKeywords.isEmpty() ? ""
                        : " Consider elaborating on: " + String.join(", ", missedKeywords) + ".";
                eval = "Good answer with solid coverage of core concepts." + hint;
            } else if (score >= 50) {
                String hint = missedKeywords.isEmpty() ? ""
                        : " Key areas to strengthen: " + String.join(", ", missedKeywords) + ".";
                eval = "Partial understanding demonstrated." + hint
                        + " Expand on edge cases and trade-offs for senior roles.";
            } else {
                String hint = missedKeywords.isEmpty() ? ""
                        : " Focus on: " + String.join(", ", missedKeywords) + ".";
                eval = "The answer missed several key concepts expected for this topic." + hint;
            }
            status = score >= 50 ? "correct" : "wrong";
        }

        resp.setScore(score);
        resp.setAiEvaluation(eval);
        resp.setStatus(status);

        return responseRepository.save(resp);
    }

    @Transactional
    public InterviewEvaluationResponse completeInterview(Long interviewId) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new RuntimeException("Interview not found"));

        List<InterviewQuestion> questions = questionRepository.findByInterviewIdOrderByQuestionOrderAsc(interviewId);
        List<QuestionFeedbackItem> feedbackItems = new ArrayList<>();

        int totalScore = 0;
        int totalQuestions = questions.size();
        int attempted = 0;
        int correct = 0;
        int wrong = 0;
        int unattempted = 0;

        for (InterviewQuestion q : questions) {
            var respOpt = responseRepository.findByQuestionId(q.getId());
            if (respOpt.isPresent()) {
                InterviewResponse r = respOpt.get();
                if ("unattempted".equals(r.getStatus())) {
                    unattempted++;
                    feedbackItems.add(new QuestionFeedbackItem(q.getId(), q.getQuestionText(), "(Not answered)", 0,
                            r.getAiEvaluation(), "unattempted"));
                } else {
                    attempted++;
                    if ("correct".equals(r.getStatus())) {
                        correct++;
                    } else {
                        wrong++;
                    }
                    totalScore += r.getScore();
                    feedbackItems.add(new QuestionFeedbackItem(q.getId(), q.getQuestionText(), r.getTranscript(),
                            r.getScore(), r.getAiEvaluation(), r.getStatus()));
                }
            } else {
                // Not answered at all
                unattempted++;
                feedbackItems.add(new QuestionFeedbackItem(q.getId(), q.getQuestionText(), "(Not answered)", 0,
                        "Unattempted", "unattempted"));
            }
        }

        int accuracy = 0;
        if (attempted > 0) {
            accuracy = (int) Math.round(((double) correct / attempted) * 100);
        }

        // Divide by ATTEMPTED questions for other scores so unattempted doesn't drag
        // them down to 0 inappropriately,
        // OR keep it as total if we still want that. The prompt said "Accuracy must be
        // calculated based ONLY on questions that the candidate actually attempted...
        // Do NOT divide by the total number of questions when calculating accuracy."
        int avgScore = attempted > 0 ? totalScore / attempted : 0;
        int techScore = Math.min(100, avgScore + 4);
        int relevanceScore = Math.min(100, avgScore + 2);
        int commScore = Math.max(0, avgScore - 1);
        int problemScore = Math.max(0, avgScore - 4);
        int projScore = Math.min(100, avgScore);

        interview.setStatus("COMPLETED");
        interview.setOverallScore(avgScore);
        interview.setTechnicalScore(techScore);
        interview.setRelevanceScore(relevanceScore);
        interview.setCommunicationScore(commScore);
        interview.setProblemSolvingScore(problemScore);
        interview.setProjectUnderstandingScore(projScore);
        interview.setTotalQuestions(totalQuestions);
        interview.setAttempted(attempted);
        interview.setCorrect(correct);
        interview.setWrong(wrong);
        interview.setUnattempted(unattempted);
        interview.setAccuracy(accuracy);
        // Generate dynamic feedback based on actual performance
        String overallFeedback;
        if (avgScore >= 85) {
            overallFeedback = "Outstanding performance! Strong technical depth with clear articulation of trade-offs. Highly ready for technical rounds.";
        } else if (avgScore >= 70) {
            overallFeedback = "Good performance with solid coverage of core concepts. Work on elaborating edge cases and system design trade-offs.";
        } else if (avgScore >= 50) {
            overallFeedback = "Developing performance. Review fundamental concepts and practice structuring answers with concrete examples.";
        } else {
            overallFeedback = "Needs improvement. Focus on core technical fundamentals and practice articulating ideas clearly before the next round.";
        }
        interview.setFeedback(overallFeedback);
        interviewRepository.save(interview);

        // Update student readiness
        careerReadinessService.calculateReadiness(interview.getStudent().getId());

        InterviewEvaluationResponse response = new InterviewEvaluationResponse();
        response.setInterviewId(interview.getId());
        response.setTitle(interview.getTitle());
        response.setOverallScore(avgScore);
        response.setTechnicalScore(techScore);
        response.setRelevanceScore(relevanceScore);
        response.setCommunicationScore(commScore);
        response.setProblemSolvingScore(problemScore);
        response.setProjectUnderstandingScore(projScore);
        response.setTotalQuestions(totalQuestions);
        response.setAttempted(attempted);
        response.setCorrect(correct);
        response.setWrong(wrong);
        response.setUnattempted(unattempted);
        response.setAccuracy(accuracy);
        response.setFeedback(overallFeedback);
        response.setQuestionEvaluations(feedbackItems);

        return response;
    }
}
