package com.smarthire.dto;

import java.util.List;

public class PlacementDtos {

    public static class BatchStatsResponse {
        private Integer batchYear = 2027;
        private Integer totalStudents = 842;
        private Integer resumeReady = 683;
        private Integer interviewReady = 491;
        private Integer placementReady = 438;
        private List<SkillGapItem> skillGaps;
        private List<WorkshopSuggestion> workshopSuggestions;

        public BatchStatsResponse() {}

        public Integer getBatchYear() { return batchYear; }
        public void setBatchYear(Integer batchYear) { this.batchYear = batchYear; }
        public Integer getTotalStudents() { return totalStudents; }
        public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }
        public Integer getResumeReady() { return resumeReady; }
        public void setResumeReady(Integer resumeReady) { this.resumeReady = resumeReady; }
        public Integer getInterviewReady() { return interviewReady; }
        public void setInterviewReady(Integer interviewReady) { this.interviewReady = interviewReady; }
        public Integer getPlacementReady() { return placementReady; }
        public void setPlacementReady(Integer placementReady) { this.placementReady = placementReady; }
        public List<SkillGapItem> getSkillGaps() { return skillGaps; }
        public void setSkillGaps(List<SkillGapItem> skillGaps) { this.skillGaps = skillGaps; }
        public List<WorkshopSuggestion> getWorkshopSuggestions() { return workshopSuggestions; }
        public void setWorkshopSuggestions(List<WorkshopSuggestion> workshopSuggestions) { this.workshopSuggestions = workshopSuggestions; }
    }

    public static class SkillGapItem {
        private String skillName;
        private String gapLevel; // "High Gap", "Medium", "Strong"
        private Integer averageProficiency;
        private Integer studentsNeedingSupport;

        public SkillGapItem() {}
        public SkillGapItem(String skillName, String gapLevel, Integer averageProficiency, Integer studentsNeedingSupport) {
            this.skillName = skillName;
            this.gapLevel = gapLevel;
            this.averageProficiency = averageProficiency;
            this.studentsNeedingSupport = studentsNeedingSupport;
        }

        public String getSkillName() { return skillName; }
        public void setSkillName(String skillName) { this.skillName = skillName; }
        public String getGapLevel() { return gapLevel; }
        public void setGapLevel(String gapLevel) { this.gapLevel = gapLevel; }
        public Integer getAverageProficiency() { return averageProficiency; }
        public void setAverageProficiency(Integer averageProficiency) { this.averageProficiency = averageProficiency; }
        public Integer getStudentsNeedingSupport() { return studentsNeedingSupport; }
        public void setStudentsNeedingSupport(Integer studentsNeedingSupport) { this.studentsNeedingSupport = studentsNeedingSupport; }
    }

    public static class WorkshopSuggestion {
        private String title;
        private String focusArea;
        private Integer studentsAffected;
        private String recommendedTimeline;

        public WorkshopSuggestion() {}
        public WorkshopSuggestion(String title, String focusArea, Integer studentsAffected, String recommendedTimeline) {
            this.title = title;
            this.focusArea = focusArea;
            this.studentsAffected = studentsAffected;
            this.recommendedTimeline = recommendedTimeline;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getFocusArea() { return focusArea; }
        public void setFocusArea(String focusArea) { this.focusArea = focusArea; }
        public Integer getStudentsAffected() { return studentsAffected; }
        public void setStudentsAffected(Integer studentsAffected) { this.studentsAffected = studentsAffected; }
        public String getRecommendedTimeline() { return recommendedTimeline; }
        public void setRecommendedTimeline(String recommendedTimeline) { this.recommendedTimeline = recommendedTimeline; }
    }

    public static class RecruiterTalentMetricsResponse {
        private Integer applicantsCount = 128;
        private Integer strongMatchesCount = 42;
        private Integer interviewReadyCount = 18;
        private Integer selectedCount = 7;
        private List<CandidateItem> candidates;

        public RecruiterTalentMetricsResponse() {}

        public Integer getApplicantsCount() { return applicantsCount; }
        public void setApplicantsCount(Integer applicantsCount) { this.applicantsCount = applicantsCount; }
        public Integer getStrongMatchesCount() { return strongMatchesCount; }
        public void setStrongMatchesCount(Integer strongMatchesCount) { this.strongMatchesCount = strongMatchesCount; }
        public Integer getInterviewReadyCount() { return interviewReadyCount; }
        public void setInterviewReadyCount(Integer interviewReadyCount) { this.interviewReadyCount = interviewReadyCount; }
        public Integer getSelectedCount() { return selectedCount; }
        public void setSelectedCount(Integer selectedCount) { this.selectedCount = selectedCount; }
        public List<CandidateItem> getCandidates() { return candidates; }
        public void setCandidates(List<CandidateItem> candidates) { this.candidates = candidates; }
    }

    public static class CandidateItem {
        private Long studentId;
        private String fullName;
        private String targetRole;
        private Integer jobMatch;
        private Integer readiness;
        private Integer skillsScore;
        private Integer evidenceScore;
        private Integer assessmentScore;
        private String experience;
        private String applicationStatus;

        public CandidateItem() {}
        public CandidateItem(Long studentId, String fullName, String targetRole, Integer jobMatch, Integer readiness, Integer skillsScore, Integer evidenceScore, Integer assessmentScore, String experience, String applicationStatus) {
            this.studentId = studentId;
            this.fullName = fullName;
            this.targetRole = targetRole;
            this.jobMatch = jobMatch;
            this.readiness = readiness;
            this.skillsScore = skillsScore;
            this.evidenceScore = evidenceScore;
            this.assessmentScore = assessmentScore;
            this.experience = experience;
            this.applicationStatus = applicationStatus;
        }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getTargetRole() { return targetRole; }
        public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
        public Integer getJobMatch() { return jobMatch; }
        public void setJobMatch(Integer jobMatch) { this.jobMatch = jobMatch; }
        public Integer getReadiness() { return readiness; }
        public void setReadiness(Integer readiness) { this.readiness = readiness; }
        public Integer getSkillsScore() { return skillsScore; }
        public void setSkillsScore(Integer skillsScore) { this.skillsScore = skillsScore; }
        public Integer getEvidenceScore() { return evidenceScore; }
        public void setEvidenceScore(Integer evidenceScore) { this.evidenceScore = evidenceScore; }
        public Integer getAssessmentScore() { return assessmentScore; }
        public void setAssessmentScore(Integer assessmentScore) { this.assessmentScore = assessmentScore; }
        public String getExperience() { return experience; }
        public void setExperience(String experience) { this.experience = experience; }
        public String getApplicationStatus() { return applicationStatus; }
        public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
    }
}
