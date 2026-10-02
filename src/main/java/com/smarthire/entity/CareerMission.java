package com.smarthire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "career_missions")
public class CareerMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @Column(nullable = false)
    private String title; // "Build a Spring Boot REST API"

    @Column(length = 2000)
    private String description;

    private String category; // Skill Evidence, GitHub, Interview Prep, Resume Polish
    private Integer estimatedMinutes = 25;
    private Integer points = 4; // Readiness points to add
    private Boolean completed = false;

    private String targetSkill = "Spring Boot";
    private Integer evidenceDelta = 5; // e.g. 65% -> 70%

    private LocalDate dateAssigned;
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        if (this.dateAssigned == null) {
            this.dateAssigned = LocalDate.now();
        }
    }

    public CareerMission() {}

    public CareerMission(StudentProfile student, String title, String description, String category, Integer estimatedMinutes, Integer points, String targetSkill, Integer evidenceDelta) {
        this.student = student;
        this.title = title;
        this.description = description;
        this.category = category;
        this.estimatedMinutes = estimatedMinutes;
        this.points = points;
        this.targetSkill = targetSkill;
        this.evidenceDelta = evidenceDelta;
        this.dateAssigned = LocalDate.now();
        this.completed = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Integer getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Integer estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }

    public String getTargetSkill() { return targetSkill; }
    public void setTargetSkill(String targetSkill) { this.targetSkill = targetSkill; }

    public Integer getEvidenceDelta() { return evidenceDelta; }
    public void setEvidenceDelta(Integer evidenceDelta) { this.evidenceDelta = evidenceDelta; }

    public LocalDate getDateAssigned() { return dateAssigned; }
    public void setDateAssigned(LocalDate dateAssigned) { this.dateAssigned = dateAssigned; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}
