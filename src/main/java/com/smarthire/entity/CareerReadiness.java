package com.smarthire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "career_readiness")
public class CareerReadiness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    private Integer overallScore = 84;
    private Integer skillsScore = 89;
    private Integer evidenceScore = 82;
    private Integer projectsScore = 91;
    private Integer interviewScore = 76;
    private Integer experienceScore = 65;

    @Column(length = 2000)
    private String summaryText;

    private LocalDateTime evaluatedAt;

    @PrePersist
    protected void onCreate() {
        this.evaluatedAt = LocalDateTime.now();
    }

    public CareerReadiness() {}

    public CareerReadiness(StudentProfile student, Integer overallScore, Integer skillsScore, Integer evidenceScore, Integer projectsScore, Integer interviewScore, Integer experienceScore) {
        this.student = student;
        this.overallScore = overallScore;
        this.skillsScore = skillsScore;
        this.evidenceScore = evidenceScore;
        this.projectsScore = projectsScore;
        this.interviewScore = interviewScore;
        this.experienceScore = experienceScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public Integer getSkillsScore() { return skillsScore; }
    public void setSkillsScore(Integer skillsScore) { this.skillsScore = skillsScore; }

    public Integer getEvidenceScore() { return evidenceScore; }
    public void setEvidenceScore(Integer evidenceScore) { this.evidenceScore = evidenceScore; }

    public Integer getProjectsScore() { return projectsScore; }
    public void setProjectsScore(Integer projectsScore) { this.projectsScore = projectsScore; }

    public Integer getInterviewScore() { return interviewScore; }
    public void setInterviewScore(Integer interviewScore) { this.interviewScore = interviewScore; }

    public Integer getExperienceScore() { return experienceScore; }
    public void setExperienceScore(Integer experienceScore) { this.experienceScore = experienceScore; }

    public String getSummaryText() { return summaryText; }
    public void setSummaryText(String summaryText) { this.summaryText = summaryText; }

    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
