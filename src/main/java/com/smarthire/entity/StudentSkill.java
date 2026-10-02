package com.smarthire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "student_skills")
public class StudentSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    // Proof-of-skill ratings (0-100)
    private Integer claimLevel = 80;
    private Integer assessmentScore = 0;
    private Integer projectEvidenceScore = 0;
    private Integer gitHubEvidenceScore = 0;
    private Integer interviewScore = 0;
    private Integer evidenceStrength = 0;
    private Boolean verified = false;

    public StudentSkill() {}

    public StudentSkill(StudentProfile student, Skill skill, Integer claimLevel) {
        this.student = student;
        this.skill = skill;
        this.claimLevel = claimLevel;
        calculateEvidenceStrength();
    }

    public void calculateEvidenceStrength() {
        int count = 0;
        int sum = 0;
        if (assessmentScore != null && assessmentScore > 0) { sum += assessmentScore * 1.5; count += 1.5; }
        if (projectEvidenceScore != null && projectEvidenceScore > 0) { sum += projectEvidenceScore * 1.2; count += 1.2; }
        if (gitHubEvidenceScore != null && gitHubEvidenceScore > 0) { sum += gitHubEvidenceScore * 1.0; count += 1.0; }
        if (interviewScore != null && interviewScore > 0) { sum += interviewScore * 1.3; count += 1.3; }

        if (count > 0) {
            this.evidenceStrength = (int) Math.round(sum / count);
            this.verified = this.evidenceStrength >= 70;
        } else {
            this.evidenceStrength = Math.min(claimLevel, 30); // Claim alone without proof is capped low
            this.verified = false;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public Skill getSkill() { return skill; }
    public void setSkill(Skill skill) { this.skill = skill; }

    public Integer getClaimLevel() { return claimLevel; }
    public void setClaimLevel(Integer claimLevel) { this.claimLevel = claimLevel; }

    public Integer getAssessmentScore() { return assessmentScore; }
    public void setAssessmentScore(Integer assessmentScore) { 
        this.assessmentScore = assessmentScore; 
        calculateEvidenceStrength();
    }

    public Integer getProjectEvidenceScore() { return projectEvidenceScore; }
    public void setProjectEvidenceScore(Integer projectEvidenceScore) { 
        this.projectEvidenceScore = projectEvidenceScore; 
        calculateEvidenceStrength();
    }

    public Integer getGitHubEvidenceScore() { return gitHubEvidenceScore; }
    public void setGitHubEvidenceScore(Integer gitHubEvidenceScore) { 
        this.gitHubEvidenceScore = gitHubEvidenceScore; 
        calculateEvidenceStrength();
    }

    public Integer getInterviewScore() { return interviewScore; }
    public void setInterviewScore(Integer interviewScore) { 
        this.interviewScore = interviewScore; 
        calculateEvidenceStrength();
    }

    public Integer getEvidenceStrength() { return evidenceStrength; }
    public void setEvidenceStrength(Integer evidenceStrength) { this.evidenceStrength = evidenceStrength; }

    public Boolean getVerified() { return verified; }
    public void setVerified(Boolean verified) { this.verified = verified; }
}
