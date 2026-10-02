package com.smarthire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "interview_questions")
public class InterviewQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    private Interview interview;

    private Integer questionOrder;

    @Column(length = 2000, nullable = false)
    private String questionText;

    private String topic; // JWT, Microservices, Spring Boot, SQL, Concurrency

    @Column(length = 3000)
    private String idealAnswer;

    public InterviewQuestion() {}

    public InterviewQuestion(Interview interview, Integer questionOrder, String questionText, String topic, String idealAnswer) {
        this.interview = interview;
        this.questionOrder = questionOrder;
        this.questionText = questionText;
        this.topic = topic;
        this.idealAnswer = idealAnswer;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Interview getInterview() { return interview; }
    public void setInterview(Interview interview) { this.interview = interview; }

    public Integer getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getIdealAnswer() { return idealAnswer; }
    public void setIdealAnswer(String idealAnswer) { this.idealAnswer = idealAnswer; }
}
