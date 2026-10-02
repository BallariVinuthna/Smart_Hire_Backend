package com.smarthire.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "career_passports")
public class CareerPassport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private StudentProfile student;

    @Column(nullable = false, unique = true)
    private String passportId; // e.g. "SHX-BV-2027"

    @Column(columnDefinition = "TEXT")
    private String qrCodeData;

    @Column(columnDefinition = "TEXT")
    private String verifiedBadgesJson;

    private Boolean isPublic = true;
    private LocalDateTime issuedAt;
    private LocalDateTime lastVerifiedAt;

    @PrePersist
    protected void onCreate() {
        this.issuedAt = LocalDateTime.now();
        this.lastVerifiedAt = LocalDateTime.now();
    }

    public CareerPassport() {}

    public CareerPassport(StudentProfile student, String passportId) {
        this.student = student;
        this.passportId = passportId;
        this.isPublic = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfile getStudent() { return student; }
    public void setStudent(StudentProfile student) { this.student = student; }

    public String getPassportId() { return passportId; }
    public void setPassportId(String passportId) { this.passportId = passportId; }

    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }

    public String getVerifiedBadgesJson() { return verifiedBadgesJson; }
    public void setVerifiedBadgesJson(String verifiedBadgesJson) { this.verifiedBadgesJson = verifiedBadgesJson; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getLastVerifiedAt() { return lastVerifiedAt; }
    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) { this.lastVerifiedAt = lastVerifiedAt; }
}
