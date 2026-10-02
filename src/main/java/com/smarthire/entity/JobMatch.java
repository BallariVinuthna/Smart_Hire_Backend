package com.smarthire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_matches")
public class JobMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    private Integer overallMatchScore;
    private Integer skillMatchScore;
    private Integer readinessScore;

    @Column(length = 1000)
    private String matchedSkills; // "Java, Spring Boot, SQL"

    @Column(length = 1000)
    private String gapSkills; // "Docker, System Design"

    @Column(length = 2000)
    private String whyText;

    private LocalDateTime calculatedAt;

    @PrePersist
    protected void onCreate() {
        this.calculatedAt = LocalDateTime.now();
    }

    public JobMatch() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public Integer getOverallMatchScore() { return overallMatchScore; }
    public void setOverallMatchScore(Integer overallMatchScore) { this.overallMatchScore = overallMatchScore; }

    public Integer getSkillMatchScore() { return skillMatchScore; }
    public void setSkillMatchScore(Integer skillMatchScore) { this.skillMatchScore = skillMatchScore; }

    public Integer getReadinessScore() { return readinessScore; }
    public void setReadinessScore(Integer readinessScore) { this.readinessScore = readinessScore; }

    public String getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(String matchedSkills) { this.matchedSkills = matchedSkills; }

    public String getGapSkills() { return gapSkills; }
    public void setGapSkills(String gapSkills) { this.gapSkills = gapSkills; }

    public String getWhyText() { return whyText; }
    public void setWhyText(String whyText) { this.whyText = whyText; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
