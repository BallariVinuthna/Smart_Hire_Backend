package com.smarthire.dto;

import java.util.List;

public class InterviewDtos {

    public static class InterviewStartRequest {
        private String targetRole = "Java Full Stack Engineer";
        public InterviewStartRequest() {}
        public String getTargetRole() { return targetRole; }
        public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    }

    public static class InterviewSubmitAnswerRequest {
        private Long questionId;
        private String transcript;
        private String audioUrl;

        public InterviewSubmitAnswerRequest() {}
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public String getTranscript() { return transcript; }
        public void setTranscript(String transcript) { this.transcript = transcript; }
        public String getAudioUrl() { return audioUrl; }
        public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }
    }

    public static class InterviewEvaluationResponse {
        private Long interviewId;
        private String title;
        private Integer overallScore;
        private Integer technicalScore;
        private Integer relevanceScore;
        private Integer communicationScore;
        private Integer problemSolvingScore;
        private Integer projectUnderstandingScore;
        private Integer totalQuestions;
        private Integer attempted;
        private Integer correct;
        private Integer wrong;
        private Integer unattempted;
        private Integer accuracy;
        private String feedback;
        private List<QuestionFeedbackItem> questionEvaluations;

        public InterviewEvaluationResponse() {}

        public Long getInterviewId() { return interviewId; }
        public void setInterviewId(Long interviewId) { this.interviewId = interviewId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getOverallScore() { return overallScore; }
        public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }
        public Integer getTechnicalScore() { return technicalScore; }
        public void setTechnicalScore(Integer technicalScore) { this.technicalScore = technicalScore; }
        public Integer getRelevanceScore() { return relevanceScore; }
        public void setRelevanceScore(Integer relevanceScore) { this.relevanceScore = relevanceScore; }
        public Integer getCommunicationScore() { return communicationScore; }
        public void setCommunicationScore(Integer communicationScore) { this.communicationScore = communicationScore; }
        public Integer getProblemSolvingScore() { return problemSolvingScore; }
        public void setProblemSolvingScore(Integer problemSolvingScore) { this.problemSolvingScore = problemSolvingScore; }
        public Integer getProjectUnderstandingScore() { return projectUnderstandingScore; }
        public void setProjectUnderstandingScore(Integer projectUnderstandingScore) { this.projectUnderstandingScore = projectUnderstandingScore; }
        public Integer getTotalQuestions() { return totalQuestions; }
        public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
        public Integer getAttempted() { return attempted; }
        public void setAttempted(Integer attempted) { this.attempted = attempted; }
        public Integer getCorrect() { return correct; }
        public void setCorrect(Integer correct) { this.correct = correct; }
        public Integer getWrong() { return wrong; }
        public void setWrong(Integer wrong) { this.wrong = wrong; }
        public Integer getUnattempted() { return unattempted; }
        public void setUnattempted(Integer unattempted) { this.unattempted = unattempted; }
        public Integer getAccuracy() { return accuracy; }
        public void setAccuracy(Integer accuracy) { this.accuracy = accuracy; }
        public String getFeedback() { return feedback; }
        public void setFeedback(String feedback) { this.feedback = feedback; }
        public List<QuestionFeedbackItem> getQuestionEvaluations() { return questionEvaluations; }
        public void setQuestionEvaluations(List<QuestionFeedbackItem> questionEvaluations) { this.questionEvaluations = questionEvaluations; }
    }

    public static class QuestionFeedbackItem {
        private Long questionId;
        private String questionText;
        private String userTranscript;
        private Integer score;
        private String aiEvaluation;
        private String status;

        public QuestionFeedbackItem() {}
        public QuestionFeedbackItem(Long questionId, String questionText, String userTranscript, Integer score, String aiEvaluation, String status) {
            this.questionId = questionId;
            this.questionText = questionText;
            this.userTranscript = userTranscript;
            this.score = score;
            this.aiEvaluation = aiEvaluation;
            this.status = status;
        }

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }
        public String getUserTranscript() { return userTranscript; }
        public void setUserTranscript(String userTranscript) { this.userTranscript = userTranscript; }
        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }
        public String getAiEvaluation() { return aiEvaluation; }
        public void setAiEvaluation(String aiEvaluation) { this.aiEvaluation = aiEvaluation; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
