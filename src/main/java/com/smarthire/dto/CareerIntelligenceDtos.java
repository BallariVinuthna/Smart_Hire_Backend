package com.smarthire.dto;

import java.util.List;
import java.util.Map;

public class CareerIntelligenceDtos {

    public static class CareerDnaResponse {
        private String targetRole;
        private Integer overallReadiness;
        private List<SkillItem> skills;
        private List<ProjectItem> projects;
        private List<EvidenceItem> evidences;
        private List<ExperienceItem> experiences;

        public CareerDnaResponse() {}

        public String getTargetRole() { return targetRole; }
        public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

        public Integer getOverallReadiness() { return overallReadiness; }
        public void setOverallReadiness(Integer overallReadiness) { this.overallReadiness = overallReadiness; }

        public List<SkillItem> getSkills() { return skills; }
        public void setSkills(List<SkillItem> skills) { this.skills = skills; }

        public List<ProjectItem> getProjects() { return projects; }
        public void setProjects(List<ProjectItem> projects) { this.projects = projects; }

        public List<EvidenceItem> getEvidences() { return evidences; }
        public void setEvidences(List<EvidenceItem> evidences) { this.evidences = evidences; }

        public List<ExperienceItem> getExperiences() { return experiences; }
        public void setExperiences(List<ExperienceItem> experiences) { this.experiences = experiences; }
    }

    public static class SkillItem {
        private String name;
        private String category;
        private Integer claimLevel;
        private Integer assessmentScore;
        private Integer projectScore;
        private Integer gitHubScore;
        private Integer interviewScore;
        private Integer evidenceStrength;
        private Boolean verified;

        public SkillItem() {}
        public SkillItem(String name, String category, Integer claimLevel, Integer assessmentScore, Integer projectScore, Integer gitHubScore, Integer interviewScore, Integer evidenceStrength, Boolean verified) {
            this.name = name;
            this.category = category;
            this.claimLevel = claimLevel;
            this.assessmentScore = assessmentScore;
            this.projectScore = projectScore;
            this.gitHubScore = gitHubScore;
            this.interviewScore = interviewScore;
            this.evidenceStrength = evidenceStrength;
            this.verified = verified;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Integer getClaimLevel() { return claimLevel; }
        public void setClaimLevel(Integer claimLevel) { this.claimLevel = claimLevel; }
        public Integer getAssessmentScore() { return assessmentScore; }
        public void setAssessmentScore(Integer assessmentScore) { this.assessmentScore = assessmentScore; }
        public Integer getProjectScore() { return projectScore; }
        public void setProjectScore(Integer projectScore) { this.projectScore = projectScore; }
        public Integer getGitHubScore() { return gitHubScore; }
        public void setGitHubScore(Integer gitHubScore) { this.gitHubScore = gitHubScore; }
        public Integer getInterviewScore() { return interviewScore; }
        public void setInterviewScore(Integer interviewScore) { this.interviewScore = interviewScore; }
        public Integer getEvidenceStrength() { return evidenceStrength; }
        public void setEvidenceStrength(Integer evidenceStrength) { this.evidenceStrength = evidenceStrength; }
        public Boolean getVerified() { return verified; }
        public void setVerified(Boolean verified) { this.verified = verified; }
    }

    public static class ProjectItem {
        private Long id;
        private String title;
        private String description;
        private String technologies;
        private String evidenceLevel;
        private Boolean verified;

        public ProjectItem() {}
        public ProjectItem(Long id, String title, String description, String technologies, String evidenceLevel, Boolean verified) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.technologies = technologies;
            this.evidenceLevel = evidenceLevel;
            this.verified = verified;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getTechnologies() { return technologies; }
        public void setTechnologies(String technologies) { this.technologies = technologies; }
        public String getEvidenceLevel() { return evidenceLevel; }
        public void setEvidenceLevel(String evidenceLevel) { this.evidenceLevel = evidenceLevel; }
        public Boolean getVerified() { return verified; }
        public void setVerified(Boolean verified) { this.verified = verified; }
    }

    public static class EvidenceItem {
        private String title;
        private String source;
        private String skillName;
        private Integer confidenceScore;

        public EvidenceItem() {}
        public EvidenceItem(String title, String source, String skillName, Integer confidenceScore) {
            this.title = title;
            this.source = source;
            this.skillName = skillName;
            this.confidenceScore = confidenceScore;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getSkillName() { return skillName; }
        public void setSkillName(String skillName) { this.skillName = skillName; }
        public Integer getConfidenceScore() { return confidenceScore; }
        public void setConfidenceScore(Integer confidenceScore) { this.confidenceScore = confidenceScore; }
    }

    public static class ExperienceItem {
        private String title;
        private String company;
        private String duration;
        private String description;

        public ExperienceItem() {}
        public ExperienceItem(String title, String company, String duration, String description) {
            this.title = title;
            this.company = company;
            this.duration = duration;
            this.description = description;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getDuration() { return duration; }
        public void setDuration(String duration) { this.duration = duration; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class JobTwinComparisonResponse {
        private String targetRole;
        private String roleSummary;
        private Integer overallMatch;
        private Map<String, Integer> roleDemand; // e.g. Java: 95, Spring Boot: 90
        private Map<String, Integer> userProfile; // e.g. Java: 88, Spring Boot: 84
        private List<String> strengths;
        private List<String> gaps;
        private String recommendation;

        public JobTwinComparisonResponse() {}

        public String getTargetRole() { return targetRole; }
        public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
        public String getRoleSummary() { return roleSummary; }
        public void setRoleSummary(String roleSummary) { this.roleSummary = roleSummary; }
        public Integer getOverallMatch() { return overallMatch; }
        public void setOverallMatch(Integer overallMatch) { this.overallMatch = overallMatch; }
        public Map<String, Integer> getRoleDemand() { return roleDemand; }
        public void setRoleDemand(Map<String, Integer> roleDemand) { this.roleDemand = roleDemand; }
        public Map<String, Integer> getUserProfile() { return userProfile; }
        public void setUserProfile(Map<String, Integer> userProfile) { this.userProfile = userProfile; }
        public List<String> getStrengths() { return strengths; }
        public void setStrengths(List<String> strengths) { this.strengths = strengths; }
        public List<String> getGaps() { return gaps; }
        public void setGaps(List<String> gaps) { this.gaps = gaps; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }

    public static class WhatIfRequest {
        private List<String> addedSkills;
        public WhatIfRequest() {}
        public WhatIfRequest(List<String> addedSkills) { this.addedSkills = addedSkills; }
        public List<String> getAddedSkills() { return addedSkills; }
        public void setAddedSkills(List<String> addedSkills) { this.addedSkills = addedSkills; }
    }

    public static class WhatIfResponse {
        private Integer currentReadiness;
        private Integer projectedReadiness;
        private Integer delta;
        private List<SkillGainItem> gains;
        private String explanation;

        public WhatIfResponse() {}
        public Integer getCurrentReadiness() { return currentReadiness; }
        public void setCurrentReadiness(Integer currentReadiness) { this.currentReadiness = currentReadiness; }
        public Integer getProjectedReadiness() { return projectedReadiness; }
        public void setProjectedReadiness(Integer projectedReadiness) { this.projectedReadiness = projectedReadiness; }
        public Integer getDelta() { return delta; }
        public void setDelta(Integer delta) { this.delta = delta; }
        public List<SkillGainItem> getGains() { return gains; }
        public void setGains(List<SkillGainItem> gains) { this.gains = gains; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    public static class SkillGainItem {
        private String skill;
        private Integer addedPoints;
        private String reason;

        public SkillGainItem() {}
        public SkillGainItem(String skill, Integer addedPoints, String reason) {
            this.skill = skill;
            this.addedPoints = addedPoints;
            this.reason = reason;
        }
        public String getSkill() { return skill; }
        public void setSkill(String skill) { this.skill = skill; }
        public Integer getAddedPoints() { return addedPoints; }
        public void setAddedPoints(Integer addedPoints) { this.addedPoints = addedPoints; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}
