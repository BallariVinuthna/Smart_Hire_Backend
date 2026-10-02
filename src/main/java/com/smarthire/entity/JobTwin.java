package com.smarthire.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "job_twins")
public class JobTwin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String targetRole; // e.g. "Java Full Stack Engineer"

    @Column(length = 2000)
    private String roleSummary;

    // JSON string representation of skill benchmarks: {"Java":95,"Spring Boot":90,"React":82,"SQL":85,"Docker":65,"System Design":60}
    @Column(length = 3000)
    private String coreSkillsWeights;

    private String experienceBenchmark;
    private Integer marketDemandScore = 92;

    @Column(length = 1000)
    private String certificationsRecommended;

    public JobTwin() {}

    public JobTwin(String targetRole, String roleSummary, String coreSkillsWeights, String experienceBenchmark, Integer marketDemandScore) {
        this.targetRole = targetRole;
        this.roleSummary = roleSummary;
        this.coreSkillsWeights = coreSkillsWeights;
        this.experienceBenchmark = experienceBenchmark;
        this.marketDemandScore = marketDemandScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getRoleSummary() { return roleSummary; }
    public void setRoleSummary(String roleSummary) { this.roleSummary = roleSummary; }

    public String getCoreSkillsWeights() { return coreSkillsWeights; }
    public void setCoreSkillsWeights(String coreSkillsWeights) { this.coreSkillsWeights = coreSkillsWeights; }

    public String getExperienceBenchmark() { return experienceBenchmark; }
    public void setExperienceBenchmark(String experienceBenchmark) { this.experienceBenchmark = experienceBenchmark; }

    public Integer getMarketDemandScore() { return marketDemandScore; }
    public void setMarketDemandScore(Integer marketDemandScore) { this.marketDemandScore = marketDemandScore; }

    public String getCertificationsRecommended() { return certificationsRecommended; }
    public void setCertificationsRecommended(String certificationsRecommended) { this.certificationsRecommended = certificationsRecommended; }
}
