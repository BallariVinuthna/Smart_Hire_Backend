package com.smarthire.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // "Java Backend Assessment"

    private String skillCategory; // Java, Spring Boot, React, SQL, Cloud
    private String difficulty = "Adaptive"; // Beginner, Intermediate, Advanced, Adaptive
    private Integer durationMinutes = 20;
    private Integer totalQuestions = 10;

    public Assessment() {}

    public Assessment(String title, String skillCategory, String difficulty, Integer durationMinutes, Integer totalQuestions) {
        this.title = title;
        this.skillCategory = skillCategory;
        this.difficulty = difficulty;
        this.durationMinutes = durationMinutes;
        this.totalQuestions = totalQuestions;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSkillCategory() { return skillCategory; }
    public void setSkillCategory(String skillCategory) { this.skillCategory = skillCategory; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }
}
