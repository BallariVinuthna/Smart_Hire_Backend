package com.smarthire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interviews")
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @Column(nullable = false)
    private String title; // "Java Full Stack AI Mock Interview"

    private String targetRole = "Java Full Stack Engineer";
    private String status = "IN_PROGRESS"; // IN_PROGRESS, COMPLETED

    // 5-Dimension Interview Readiness Scores (0-100)
    private Integer overallScore = 76;
    private Integer technicalScore = 80;
    private Integer relevanceScore = 78;
    private Integer communicationScore = 75;
    private Integer problemSolvingScore = 72;
    private Integer projectUnderstandingScore = 75;

    @Column(length = 3000)
    private String feedback;

    private Integer totalQuestions = 0;
    private Integer attempted = 0;
    private Integer correct = 0;
    private Integer wrong = 0;
    private Integer unattempted = 0;
    private Integer accuracy = 0;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Interview() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getOverallScore() { return overallScore; }
    public void setOverallScore(Integer overallScore) { this.overallScore = overallScore; }

    public Integer getTechnicalScore() { return technicalScore; }
    public void setTechnicalScore(Integer technicalScore) { this.technicalScore = technicalScore; }

    public Integer getRelevanceScore() { return relevanceScore; }
    public void setRelevanceScore(Integer relevanceScore) { this.relevanceScore = relevanceScore; }

    public Integer getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(Integer communicationScore) { this.communicationScore = communicationScore; }

    public Integer getProblemSolvingScore() { return problemSolvingScore; }
    public void setProblemSolvingScore(Integer problemSolvingScore) { this.problemSolvingScore = problemSolvingScore; }

    public Integer getProjectUnderstandingScore() { return projectUnderstandingScore; }
    public void setProjectUnderstandingScore(Integer projectUnderstandingScore) { this.projectUnderstandingScore = projectUnderstandingScore; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public Integer getAttempted() { return attempted; }
    public void setAttempted(Integer attempted) { this.attempted = attempted; }

    public Integer getCorrect() { return correct; }
    public void setCorrect(Integer correct) { this.correct = correct; }

    public Integer getWrong() { return wrong; }
    public void setWrong(Integer wrong) { this.wrong = wrong; }

    public Integer getUnattempted() { return unattempted; }
    public void setUnattempted(Integer unattempted) { this.unattempted = unattempted; }

    public Integer getAccuracy() { return accuracy; }
    public void setAccuracy(Integer accuracy) { this.accuracy = accuracy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
