package com.smarthire.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AiDtos {

    public static class ChatRequest {
        private String message;
        private String conversationId;
        private String documentId;

        public ChatRequest() {}

        public ChatRequest(String message) {
            this.message = message;
        }

        public ChatRequest(String message, String conversationId) {
            this.message = message;
            this.conversationId = conversationId;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }

        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }
    }

    public static class ChatResponse {
        private String response;
        private String conversationId;
        private List<SourceCitation> sources;

        public ChatResponse() {}

        public ChatResponse(String response) {
            this.response = response;
        }

        public ChatResponse(String response, String conversationId, List<SourceCitation> sources) {
            this.response = response;
            this.conversationId = conversationId;
            this.sources = sources;
        }

        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }

        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }

        public List<SourceCitation> getSources() { return sources; }
        public void setSources(List<SourceCitation> sources) { this.sources = sources; }
    }

    public static class SourceCitation {
        private String documentId;
        private String filename;
        private Integer chunkIndex;
        private Double similarityScore;
        private String textSnippet;

        public SourceCitation() {}

        public SourceCitation(String documentId, String filename, Integer chunkIndex, Double similarityScore, String textSnippet) {
            this.documentId = documentId;
            this.filename = filename;
            this.chunkIndex = chunkIndex;
            this.similarityScore = similarityScore;
            this.textSnippet = textSnippet;
        }

        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }

        public Integer getChunkIndex() { return chunkIndex; }
        public void setChunkIndex(Integer chunkIndex) { this.chunkIndex = chunkIndex; }

        public Double getSimilarityScore() { return similarityScore; }
        public void setSimilarityScore(Double similarityScore) { this.similarityScore = similarityScore; }

        public String getTextSnippet() { return textSnippet; }
        public void setTextSnippet(String textSnippet) { this.textSnippet = textSnippet; }
    }

    public static class DocumentUploadResponse {
        private Long id;
        private String documentId;
        private String filename;
        private Long fileSize;
        private String docType;
        private String status;
        private Integer characterCount;
        private Integer chunkCount;
        private String message;
        private LocalDateTime uploadedAt;

        public DocumentUploadResponse() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getDocumentId() { return documentId; }
        public void setDocumentId(String documentId) { this.documentId = documentId; }

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }

        public Long getFileSize() { return fileSize; }
        public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

        public String getDocType() { return docType; }
        public void setDocType(String docType) { this.docType = docType; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Integer getCharacterCount() { return characterCount; }
        public void setCharacterCount(Integer characterCount) { this.characterCount = characterCount; }

        public Integer getChunkCount() { return chunkCount; }
        public void setChunkCount(Integer chunkCount) { this.chunkCount = chunkCount; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public LocalDateTime getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    }

    public static class ResumeMatchRequest {
        private String resumeDocumentId;
        private String jobDocumentId;
        private String jobDescriptionText;

        public ResumeMatchRequest() {}

        public String getResumeDocumentId() { return resumeDocumentId; }
        public void setResumeDocumentId(String resumeDocumentId) { this.resumeDocumentId = resumeDocumentId; }

        public String getJobDocumentId() { return jobDocumentId; }
        public void setJobDocumentId(String jobDocumentId) { this.jobDocumentId = jobDocumentId; }

        public String getJobDescriptionText() { return jobDescriptionText; }
        public void setJobDescriptionText(String jobDescriptionText) { this.jobDescriptionText = jobDescriptionText; }
    }

    public static class ResumeMatchResponse {
        private Integer matchPercentage;
        private String matchVerdict; // "Strong Match", "Moderate Match", "Low Match"
        private String candidateSummary;
        private List<String> matchedSkills;
        private List<String> missingSkills;
        private List<String> keyStrengths;
        private List<String> potentialGaps;
        private List<String> interviewQuestions;

        public ResumeMatchResponse() {}

        public Integer getMatchPercentage() { return matchPercentage; }
        public void setMatchPercentage(Integer matchPercentage) { this.matchPercentage = matchPercentage; }

        public String getMatchVerdict() { return matchVerdict; }
        public void setMatchVerdict(String matchVerdict) { this.matchVerdict = matchVerdict; }

        public String getCandidateSummary() { return candidateSummary; }
        public void setCandidateSummary(String candidateSummary) { this.candidateSummary = candidateSummary; }

        public List<String> getMatchedSkills() { return matchedSkills; }
        public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }

        public List<String> getMissingSkills() { return missingSkills; }
        public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }

        public List<String> getKeyStrengths() { return keyStrengths; }
        public void setKeyStrengths(List<String> keyStrengths) { this.keyStrengths = keyStrengths; }

        public List<String> getPotentialGaps() { return potentialGaps; }
        public void setPotentialGaps(List<String> potentialGaps) { this.potentialGaps = potentialGaps; }

        public List<String> getInterviewQuestions() { return interviewQuestions; }
        public void setInterviewQuestions(List<String> interviewQuestions) { this.interviewQuestions = interviewQuestions; }
    }

    public static class ConversationSummaryDto {
        private String conversationId;
        private String title;
        private int messageCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ConversationSummaryDto() {}

        public ConversationSummaryDto(String conversationId, String title, int messageCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.conversationId = conversationId;
            this.title = title;
            this.messageCount = messageCount;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public String getConversationId() { return conversationId; }
        public void setConversationId(String conversationId) { this.conversationId = conversationId; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public int getMessageCount() { return messageCount; }
        public void setMessageCount(int messageCount) { this.messageCount = messageCount; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
}
