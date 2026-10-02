package com.smarthire.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "placement_drives")
public class PlacementDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false)
    private String title; // "Google / Apex Systems Campus Drive 2027"

    private Integer batchYear = 2027;
    private Double minCgpa = 7.0;
    private Integer minReadiness = 75;
    private LocalDate driveDate;
    private String location = "Campus Auditorium / Virtual";
    private String packageOffered = "18 - 24 LPA";
    private String status = "UPCOMING"; // UPCOMING, ONGOING, COMPLETED
    private String rolesOffered = "Software Engineer, Full Stack Developer";

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public PlacementDrive() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getBatchYear() { return batchYear; }
    public void setBatchYear(Integer batchYear) { this.batchYear = batchYear; }

    public Double getMinCgpa() { return minCgpa; }
    public void setMinCgpa(Double minCgpa) { this.minCgpa = minCgpa; }

    public Integer getMinReadiness() { return minReadiness; }
    public void setMinReadiness(Integer minReadiness) { this.minReadiness = minReadiness; }

    public LocalDate getDriveDate() { return driveDate; }
    public void setDriveDate(LocalDate driveDate) { this.driveDate = driveDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPackageOffered() { return packageOffered; }
    public void setPackageOffered(String packageOffered) { this.packageOffered = packageOffered; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRolesOffered() { return rolesOffered; }
    public void setRolesOffered(String rolesOffered) { this.rolesOffered = rolesOffered; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
