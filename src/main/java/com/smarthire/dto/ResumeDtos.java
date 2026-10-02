package com.smarthire.dto;

import java.util.List;

public class ResumeDtos {

    public static class ResumeRequest {
        private String title;
        private String templateType = "Modern";
        private Boolean isPrimary = false;
        private String contentJson;

        public ResumeRequest() {}

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getTemplateType() { return templateType; }
        public void setTemplateType(String templateType) { this.templateType = templateType; }
        public Boolean getIsPrimary() { return isPrimary; }
        public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
        public String getContentJson() { return contentJson; }
        public void setContentJson(String contentJson) { this.contentJson = contentJson; }
    }

    public static class AtsAnalysisResponse {
        private Long resumeId;
        private Integer atsScore;
        private Integer keywordsScore;
        private Integer structureScore;
        private Integer formattingScore;
        private Integer skillsScore;
        private Integer readabilityScore;
        private Integer impactScore;
        private Integer wordCount;
        private String detectedEmail;
        private String detectedPhone;
        private String overallTier;
        private List<String> detectedSections;
        private List<String> matchedKeywords;
        private List<String> missingKeywords;
        private List<String> suggestions;
        private List<TruthFindingItem> truthFindings;

        public AtsAnalysisResponse() {}

        public Long getResumeId() { return resumeId; }
        public void setResumeId(Long resumeId) { this.resumeId = resumeId; }
        public Integer getAtsScore() { return atsScore; }
        public void setAtsScore(Integer atsScore) { this.atsScore = atsScore; }
        public Integer getKeywordsScore() { return keywordsScore; }
        public void setKeywordsScore(Integer keywordsScore) { this.keywordsScore = keywordsScore; }
        public Integer getStructureScore() { return structureScore; }
        public void setStructureScore(Integer structureScore) { this.structureScore = structureScore; }
        public Integer getFormattingScore() { return formattingScore; }
        public void setFormattingScore(Integer formattingScore) { this.formattingScore = formattingScore; }
        public Integer getSkillsScore() { return skillsScore; }
        public void setSkillsScore(Integer skillsScore) { this.skillsScore = skillsScore; }
        public Integer getReadabilityScore() { return readabilityScore; }
        public void setReadabilityScore(Integer readabilityScore) { this.readabilityScore = readabilityScore; }
        public Integer getImpactScore() { return impactScore; }
        public void setImpactScore(Integer impactScore) { this.impactScore = impactScore; }
        public Integer getWordCount() { return wordCount; }
        public void setWordCount(Integer wordCount) { this.wordCount = wordCount; }
        public String getDetectedEmail() { return detectedEmail; }
        public void setDetectedEmail(String detectedEmail) { this.detectedEmail = detectedEmail; }
        public String getDetectedPhone() { return detectedPhone; }
        public void setDetectedPhone(String detectedPhone) { this.detectedPhone = detectedPhone; }
        public String getOverallTier() { return overallTier; }
        public void setOverallTier(String overallTier) { this.overallTier = overallTier; }
        public List<String> getDetectedSections() { return detectedSections; }
        public void setDetectedSections(List<String> detectedSections) { this.detectedSections = detectedSections; }
        public List<String> getMatchedKeywords() { return matchedKeywords; }
        public void setMatchedKeywords(List<String> matchedKeywords) { this.matchedKeywords = matchedKeywords; }
        public List<String> getMissingKeywords() { return missingKeywords; }
        public void setMissingKeywords(List<String> missingKeywords) { this.missingKeywords = missingKeywords; }
        public List<String> getSuggestions() { return suggestions; }
        public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
        public List<TruthFindingItem> getTruthFindings() { return truthFindings; }
        public void setTruthFindings(List<TruthFindingItem> truthFindings) { this.truthFindings = truthFindings; }
    }

    public static class TruthFindingItem {
        private String claim;
        private String projectEvidence; // High, Medium, Low, None
        private String gitHubEvidence;
        private String assessmentEvidence;
        private String status; // "Verified", "Needs Evidence", "Unverified Claim"
        private String recommendation;

        public TruthFindingItem() {}
        public TruthFindingItem(String claim, String projectEvidence, String gitHubEvidence, String assessmentEvidence, String status, String recommendation) {
            this.claim = claim;
            this.projectEvidence = projectEvidence;
            this.gitHubEvidence = gitHubEvidence;
            this.assessmentEvidence = assessmentEvidence;
            this.status = status;
            this.recommendation = recommendation;
        }

        public String getClaim() { return claim; }
        public void setClaim(String claim) { this.claim = claim; }
        public String getProjectEvidence() { return projectEvidence; }
        public void setProjectEvidence(String projectEvidence) { this.projectEvidence = projectEvidence; }
        public String getGitHubEvidence() { return gitHubEvidence; }
        public void setGitHubEvidence(String gitHubEvidence) { this.gitHubEvidence = gitHubEvidence; }
        public String getAssessmentEvidence() { return assessmentEvidence; }
        public void setAssessmentEvidence(String assessmentEvidence) { this.assessmentEvidence = assessmentEvidence; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
}
