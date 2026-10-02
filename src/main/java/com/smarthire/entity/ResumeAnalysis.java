package com.smarthire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analyses")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    private Integer atsScore = 84;
    private Integer keywordsScore = 91;
    private Integer structureScore = 87;
    private Integer formattingScore = 82;
    private Integer skillsScore = 89;
    private Integer readabilityScore = 86;

    @Column(length = 2000)
    private String matchedKeywords; // "Java, Spring Boot, REST APIs, Microservices, React, Docker"

    @Column(length = 2000)
    private String missingKeywords; // "Kubernetes, CI/CD, AWS ECS"

    @Column(columnDefinition = "TEXT")
    private String suggestionsJson;

    // Truth Checker findings
    @Column(columnDefinition = "TEXT")
    private String truthFindingsJson;

    private LocalDateTime analyzedAt;

    @PrePersist
    protected void onCreate() {
        this.analyzedAt = LocalDateTime.now();
    }

    public ResumeAnalysis() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Resume getResume() { return resume; }
    public void setResume(Resume resume) { this.resume = resume; }

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

    public String getMatchedKeywords() { return matchedKeywords; }
    public void setMatchedKeywords(String matchedKeywords) { this.matchedKeywords = matchedKeywords; }

    public String getMissingKeywords() { return missingKeywords; }
    public void setMissingKeywords(String missingKeywords) { this.missingKeywords = missingKeywords; }

    public String getSuggestionsJson() { return suggestionsJson; }
    public void setSuggestionsJson(String suggestionsJson) { this.suggestionsJson = suggestionsJson; }

    public String getTruthFindingsJson() { return truthFindingsJson; }
    public void setTruthFindingsJson(String truthFindingsJson) { this.truthFindingsJson = truthFindingsJson; }

    public LocalDateTime getAnalyzedAt() { return analyzedAt; }
    public void setAnalyzedAt(LocalDateTime analyzedAt) { this.analyzedAt = analyzedAt; }
}
