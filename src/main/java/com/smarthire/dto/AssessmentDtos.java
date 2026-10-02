package com.smarthire.dto;

import java.util.Map;

public class AssessmentDtos {

    public static class AssessmentSubmitRequest {
        private Long assessmentId;
        private Map<Long, String> answers; // questionId -> selectedOption ("A", "B", etc.)

        public AssessmentSubmitRequest() {}

        public Long getAssessmentId() { return assessmentId; }
        public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }

        public Map<Long, String> getAnswers() { return answers; }
        public void setAnswers(Map<Long, String> answers) { this.answers = answers; }
    }

    public static class AssessmentResultResponse {
        private Long attemptId;
        private String assessmentTitle;
        private Integer score;
        private Integer totalQuestions;
        private Integer correctAnswers;
        private String foundationLevel;
        private String feedback;

        public AssessmentResultResponse() {}

        public Long getAttemptId() { return attemptId; }
        public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }
        public String getAssessmentTitle() { return assessmentTitle; }
        public void setAssessmentTitle(String assessmentTitle) { this.assessmentTitle = assessmentTitle; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        public Integer getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
        public Integer getCorrectAnswers() { return correctAnswers; }
        public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }
        public String getFoundationLevel() { return foundationLevel; }
        public void setFoundationLevel(String foundationLevel) { this.foundationLevel = foundationLevel; }
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
    }
}
