package com.smarthire.service;

import com.smarthire.entity.CareerRoadmap;
import com.smarthire.entity.RoadmapNode;
import com.smarthire.entity.StudentProfile;
import com.smarthire.repository.CareerRoadmapRepository;
import com.smarthire.repository.RoadmapNodeRepository;
import com.smarthire.repository.StudentProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CareerRoadmapService {

    private final CareerRoadmapRepository roadmapRepository;
    private final RoadmapNodeRepository nodeRepository;
    private final StudentProfileRepository studentProfileRepository;

    public CareerRoadmapService(
            CareerRoadmapRepository roadmapRepository,
            RoadmapNodeRepository nodeRepository,
            StudentProfileRepository studentProfileRepository) {
        this.roadmapRepository = roadmapRepository;
        this.nodeRepository = nodeRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @Transactional
    public CareerRoadmap getOrGenerateRoadmap(Long studentId) {
        return roadmapRepository.findFirstByStudentIdOrderByGeneratedAtDesc(studentId)
                .orElseGet(() -> generateDefaultRoadmap(studentId));
    }

    @Transactional
    public CareerRoadmap generateDefaultRoadmap(Long studentId) {
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        String role = student.getTargetRole() != null ? student.getTargetRole() : "Java Full Stack Engineer";
        CareerRoadmap roadmap = new CareerRoadmap(student, role);
        roadmap = roadmapRepository.save(roadmap);

        List<RoadmapNode> nodes = List.of(
                new RoadmapNode(roadmap, 1, "Core Java & Memory Model", "JVM internals, GC, and Multithreading", "Master Java 17 records, virtual threads, CompletableFuture, and memory leaks diagnosis.", "COMPLETED", "12 hours", "Java, Concurrency, JVM"),
                new RoadmapNode(roadmap, 2, "Spring Boot & JPA Architecture", "Enterprise MVC & Data Persistence", "Build enterprise REST APIs with Spring Data JPA, connection pooling, and optimistic locking.", "COMPLETED", "15 hours", "Spring Boot, Hibernate, SQL"),
                new RoadmapNode(roadmap, 3, "Security & Microservices", "Stateless Auth & Gateway Routing", "Implement JWT token exchange, Spring Security filters, and Eureka/Resilience4j fault tolerance.", "IN_PROGRESS", "18 hours", "JWT, Microservices, Security"),
                new RoadmapNode(roadmap, 4, "Docker & Cloud Deployment", "Containerization & CI/CD Pipelines", "Containerize backend and React SPA using multi-stage Docker builds, deploy on cloud VM.", "UPCOMING", "10 hours", "Docker, CI/CD, AWS"),
                new RoadmapNode(roadmap, 5, "Distributed System Design", "Scalability, Caching & Message Queues", "Design scalable URL shorteners, distributed rate limiters, and Kafka event streams.", "UPCOMING", "14 hours", "System Design, Redis, Kafka"),
                new RoadmapNode(roadmap, 6, "AI Interview & Behavioral Mastery", "STAR Technique & Mock Rounds", "Practice top 30 architectural interview scenarios with real-time speech AI evaluations.", "UPCOMING", "8 hours", "Interviewing, Communication"),
                new RoadmapNode(roadmap, 7, "Target Role Offer: " + role, "Campus Drives & Elite Referral Placements", "Achieve 90%+ verified career readiness and secure tier-1 technology placement.", "UPCOMING", "Final Step", "Offer Acceptance")
        );

        nodeRepository.saveAll(nodes);
        roadmap.setNodes(nodes);
        return roadmap;
    }

    @Transactional
    public RoadmapNode updateNodeStatus(Long nodeId, String status) {
        RoadmapNode node = nodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Node not found"));
        node.setStatus(status);
        return nodeRepository.save(node);
    }
}
