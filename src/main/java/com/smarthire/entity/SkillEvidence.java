package com.smarthire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_evidences")
public class SkillEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_skill_id", nullable = false)
    private StudentSkill studentSkill;

    @Column(nullable = false)
    private String title;

    private String source; // GITHUB, PROJECT, ASSESSMENT, CERTIFICATE, INTERVIEW
    private String url;

    @Column(length = 2000)
    private String description;

    private Integer confidenceScore = 80;
    private LocalDateTime verifiedAt;

    @PrePersist
    protected void onCreate() {
        this.verifiedAt = LocalDateTime.now();
    }

    public SkillEvidence() {}

    public SkillEvidence(StudentSkill studentSkill, String title, String source, String url, String description, Integer confidenceScore) {
        this.studentSkill = studentSkill;
        this.title = title;
        this.source = source;
        this.url = url;
        this.description = description;
        this.confidenceScore = confidenceScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentSkill getStudentSkill() { return studentSkill; }
    public void setStudentSkill(StudentSkill studentSkill) { this.studentSkill = studentSkill; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Integer confidenceScore) { this.confidenceScore = confidenceScore; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
}
