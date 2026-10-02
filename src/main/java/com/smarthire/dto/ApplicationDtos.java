package com.smarthire.dto;

import java.util.List;

public class ApplicationDtos {

    public static class PreApplyCheckResponse {
        private Long jobId;
        private String jobTitle;
        private String companyName;
        private Integer readinessScore;
        private Integer jobMatchScore;
        private Integer resumeMatchScore;
        private Integer interviewPrepScore;
        private Boolean isStrongMatch;
        private String matchVerdict;
        private List<String> recommendedActions;

        public PreApplyCheckResponse() {}

        public Long getJobId() { return jobId; }
        public void setJobId(Long jobId) { this.jobId = jobId; }
        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
        public Integer getReadinessScore() { return readinessScore; }
        public void setReadinessScore(Integer readinessScore) { this.readinessScore = readinessScore; }
        public Integer getJobMatchScore() { return jobMatchScore; }
        public void setJobMatchScore(Integer jobMatchScore) { this.jobMatchScore = jobMatchScore; }
        public Integer getResumeMatchScore() { return resumeMatchScore; }
        public void setResumeMatchScore(Integer resumeMatchScore) { this.resumeMatchScore = resumeMatchScore; }
        public Integer getInterviewPrepScore() { return interviewPrepScore; }
        public void setInterviewPrepScore(Integer interviewPrepScore) { this.interviewPrepScore = interviewPrepScore; }
        public Boolean getIsStrongMatch() { return isStrongMatch; }
        public void setIsStrongMatch(Boolean isStrongMatch) { this.isStrongMatch = isStrongMatch; }
        public String getMatchVerdict() { return matchVerdict; }
        public void setMatchVerdict(String matchVerdict) { this.matchVerdict = matchVerdict; }
        public List<String> getRecommendedActions() { return recommendedActions; }
        public void setRecommendedActions(List<String> recommendedActions) { this.recommendedActions = recommendedActions; }
    }

    public static class ApplyJobRequest {
        private Long jobId;
        private String coverNote;

        public ApplyJobRequest() {}
        public Long getJobId() { return jobId; }
        public void setJobId(Long jobId) { this.jobId = jobId; }
        public String getCoverNote() { return coverNote; }
        public void setCoverNote(String coverNote) { this.coverNote = coverNote; }
    }
}
