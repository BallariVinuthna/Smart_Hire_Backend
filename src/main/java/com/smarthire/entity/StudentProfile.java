package com.smarthire.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String targetRole;
    private String headline;

    @Column(length = 2000)
    private String bio;

    private String university;
    private String degree;
    private String department;
    private Integer batchYear;
    private Double cgpa;

    private String githubUsername;
    @Column(length = 1000)
    private String githubAccessToken;
    private java.time.LocalDateTime githubConnectedAt;
    private String linkedinUrl;
    private String portfolioUrl;

    // Career Readiness Pillars (0-100)
    private Integer careerReadinessScore = 0;
    private Integer technicalSkillsScore = 0;
    private Integer projectEvidenceScore = 0;
    private Integer interviewReadinessScore = 0;
    private Integer experienceScore = 0;

    public StudentProfile() {}

    public StudentProfile(User user) {
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getBatchYear() { return batchYear; }
    public void setBatchYear(Integer batchYear) { this.batchYear = batchYear; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public String getGithubUsername() { return githubUsername; }
    public void setGithubUsername(String githubUsername) { this.githubUsername = githubUsername; }

    public String getGithubAccessToken() { return githubAccessToken; }
    public void setGithubAccessToken(String githubAccessToken) { this.githubAccessToken = githubAccessToken; }

    public java.time.LocalDateTime getGithubConnectedAt() { return githubConnectedAt; }
    public void setGithubConnectedAt(java.time.LocalDateTime githubConnectedAt) { this.githubConnectedAt = githubConnectedAt; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String linkedinUrl) { this.linkedinUrl = linkedinUrl; }

    public String getPortfolioUrl() { return portfolioUrl; }
    public void setPortfolioUrl(String portfolioUrl) { this.portfolioUrl = portfolioUrl; }

    public Integer getCareerReadinessScore() { return careerReadinessScore; }
    public void setCareerReadinessScore(Integer careerReadinessScore) { this.careerReadinessScore = careerReadinessScore; }

    public Integer getTechnicalSkillsScore() { return technicalSkillsScore; }
    public void setTechnicalSkillsScore(Integer technicalSkillsScore) { this.technicalSkillsScore = technicalSkillsScore; }

    public Integer getProjectEvidenceScore() { return projectEvidenceScore; }
    public void setProjectEvidenceScore(Integer projectEvidenceScore) { this.projectEvidenceScore = projectEvidenceScore; }

    public Integer getInterviewReadinessScore() { return interviewReadinessScore; }
    public void setInterviewReadinessScore(Integer interviewReadinessScore) { this.interviewReadinessScore = interviewReadinessScore; }

    public Integer getExperienceScore() { return experienceScore; }
    public void setExperienceScore(Integer experienceScore) { this.experienceScore = experienceScore; }
}
