package com.smarthire.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    private String status = "APPLIED"; // APPLIED, SHORTLISTED, ASSESSMENT, INTERVIEW, SELECTED, REJECTED
    private Integer readinessSnapshot = 84;
    private Integer jobMatchSnapshot = 91;

    @Column(length = 2000)
    private String coverNote;

    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Application() {}

    public Application(StudentProfile student, Job job, Integer readinessSnapshot, Integer jobMatchSnapshot) {
        this.student = student;
        this.job = job;
        this.readinessSnapshot = readinessSnapshot;
        this.jobMatchSnapshot = jobMatchSnapshot;
        this.status = "APPLIED";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getReadinessSnapshot() { return readinessSnapshot; }
    public void setReadinessSnapshot(Integer readinessSnapshot) { this.readinessSnapshot = readinessSnapshot; }

    public Integer getJobMatchSnapshot() { return jobMatchSnapshot; }
    public void setJobMatchSnapshot(Integer jobMatchSnapshot) { this.jobMatchSnapshot = jobMatchSnapshot; }

    public String getCoverNote() { return coverNote; }
    public void setCoverNote(String coverNote) { this.coverNote = coverNote; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
