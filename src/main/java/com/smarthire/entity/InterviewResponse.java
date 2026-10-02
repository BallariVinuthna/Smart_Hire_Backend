package com.smarthire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_responses")
public class InterviewResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private InterviewQuestion question;

    @Column(columnDefinition = "TEXT")
    private String transcript;

    private String responseAudioUrl;

    @Column(columnDefinition = "TEXT")
    private String aiEvaluation;

    private Integer score = 80;

    private String status = "unattempted"; // unattempted, correct, wrong

    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }

    public InterviewResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public InterviewQuestion getQuestion() { return question; }
    public void setQuestion(InterviewQuestion question) { this.question = question; }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public String getResponseAudioUrl() { return responseAudioUrl; }
    public void setResponseAudioUrl(String responseAudioUrl) { this.responseAudioUrl = responseAudioUrl; }

    public String getAiEvaluation() { return aiEvaluation; }
    public void setAiEvaluation(String aiEvaluation) { this.aiEvaluation = aiEvaluation; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
