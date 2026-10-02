package com.smarthire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "roadmap_nodes")
public class RoadmapNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roadmap_id", nullable = false)
    private CareerRoadmap roadmap;

    private Integer stepOrder;
    private String title;
    private String subtitle;

    @Column(length = 2000)
    private String description;

    private String status = "UPCOMING"; // COMPLETED, IN_PROGRESS, UPCOMING
    private String estimatedTime;
    private String keySkills;

    @Column(length = 2000)
    private String recommendedResources;

    public RoadmapNode() {}

    public RoadmapNode(CareerRoadmap roadmap, Integer stepOrder, String title, String subtitle, String description, String status, String estimatedTime, String keySkills) {
        this.roadmap = roadmap;
        this.stepOrder = stepOrder;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.status = status;
        this.estimatedTime = estimatedTime;
        this.keySkills = keySkills;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CareerRoadmap getRoadmap() { return roadmap; }
    public void setRoadmap(CareerRoadmap roadmap) { this.roadmap = roadmap; }

    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }

    public String getKeySkills() { return keySkills; }
    public void setKeySkills(String keySkills) { this.keySkills = keySkills; }

    public String getRecommendedResources() { return recommendedResources; }
    public void setRecommendedResources(String recommendedResources) { this.recommendedResources = recommendedResources; }
}
