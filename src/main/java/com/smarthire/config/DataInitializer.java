package com.smarthire.config;

import com.smarthire.entity.*;
import com.smarthire.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final JobTwinRepository jobTwinRepository;
    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final SkillEvidenceRepository skillEvidenceRepository;
    private final ProjectRepository projectRepository;
    private final CertificateRepository certificateRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final ResumeRepository resumeRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final AssessmentRepository assessmentRepository;
    private final AssessmentQuestionRepository questionRepository;
    private final AssessmentAttemptRepository attemptRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final InterviewResponseRepository interviewResponseRepository;
    private final CareerRoadmapRepository roadmapRepository;
    private final RoadmapNodeRepository roadmapNodeRepository;
    private final CareerMissionRepository missionRepository;
    private final ApplicationRepository applicationRepository;
    private final PlacementDriveRepository driveRepository;
    private final CareerPassportRepository passportRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            RecruiterProfileRepository recruiterProfileRepository,
            CompanyRepository companyRepository,
            JobRepository jobRepository,
            JobTwinRepository jobTwinRepository,
            SkillRepository skillRepository,
            StudentSkillRepository studentSkillRepository,
            SkillEvidenceRepository skillEvidenceRepository,
            ProjectRepository projectRepository,
            CertificateRepository certificateRepository,
            ExperienceRepository experienceRepository,
            EducationRepository educationRepository,
            ResumeRepository resumeRepository,
            ResumeVersionRepository resumeVersionRepository,
            ResumeAnalysisRepository resumeAnalysisRepository,
            AssessmentRepository assessmentRepository,
            AssessmentQuestionRepository questionRepository,
            AssessmentAttemptRepository attemptRepository,
            InterviewRepository interviewRepository,
            InterviewQuestionRepository interviewQuestionRepository,
            InterviewResponseRepository interviewResponseRepository,
            CareerRoadmapRepository roadmapRepository,
            RoadmapNodeRepository roadmapNodeRepository,
            CareerMissionRepository missionRepository,
            ApplicationRepository applicationRepository,
            PlacementDriveRepository driveRepository,
            CareerPassportRepository passportRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.jobTwinRepository = jobTwinRepository;
        this.skillRepository = skillRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.skillEvidenceRepository = skillEvidenceRepository;
        this.projectRepository = projectRepository;
        this.certificateRepository = certificateRepository;
        this.experienceRepository = experienceRepository;
        this.educationRepository = educationRepository;
        this.resumeRepository = resumeRepository;
        this.resumeVersionRepository = resumeVersionRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.assessmentRepository = assessmentRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.interviewRepository = interviewRepository;
        this.interviewQuestionRepository = interviewQuestionRepository;
        this.interviewResponseRepository = interviewResponseRepository;
        this.roadmapRepository = roadmapRepository;
        this.roadmapNodeRepository = roadmapNodeRepository;
        this.missionRepository = missionRepository;
        this.applicationRepository = applicationRepository;
        this.driveRepository = driveRepository;
        this.passportRepository = passportRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Seed data already exists
        }

        System.out.println(">>> [SmartHire X] Seeding production demo environment...");

        // 1. Core Skills
        Skill java = skillRepository.save(new Skill("Java", "Backend", 95));
        Skill springBoot = skillRepository.save(new Skill("Spring Boot", "Backend", 92));
        Skill react = skillRepository.save(new Skill("React", "Frontend", 88));
        Skill sql = skillRepository.save(new Skill("SQL", "Database", 86));
        Skill docker = skillRepository.save(new Skill("Docker", "DevOps", 82));
        Skill aws = skillRepository.save(new Skill("AWS", "Cloud", 80));
        Skill dsa = skillRepository.save(new Skill("DSA", "Core", 90));
        Skill git = skillRepository.save(new Skill("Git", "Tools", 94));
        Skill systemDesign = skillRepository.save(new Skill("System Design", "Architecture", 85));
        Skill python = skillRepository.save(new Skill("Python", "Backend", 84));

        // 2. Companies
        Company apex = companyRepository.save(new Company("Apex Systems", "Cloud Computing & Enterprise AI", "Bengaluru / Remote", "https://apexsystems.io"));
        Company google = companyRepository.save(new Company("Google Cloud", "Internet & Enterprise Infrastructure", "Hyderabad / Bengaluru", "https://cloud.google.com"));
        Company stripe = companyRepository.save(new Company("Stripe", "FinTech & Payments Infrastructure", "Remote / Singapore", "https://stripe.com"));

        // 3. Users
        String defaultPassword = passwordEncoder.encode("password123");

        // Student 1: Ballari Vinuthna (Primary Hero Persona)
        User student1User = userRepository.save(new User("student@smarthire.ai", defaultPassword, "Ballari Vinuthna", Role.ROLE_STUDENT));
        student1User.setPhone("+91 98765 43210");
        userRepository.save(student1User);

        StudentProfile student1 = new StudentProfile(student1User);
        student1.setHeadline("Full Stack Java & Cloud Systems Developer | Seeking 2027 Engineering Opportunities");
        student1.setBio("Passionate software engineer focused on building robust distributed backend services with Spring Boot and reactive modern frontends in React. Proven project evidence and open-source contribution track record.");
        student1.setUniversity("National Institute of Technology (NIT)");
        student1.setDegree("B.Tech in Computer Science & Engineering");
        student1.setDepartment("Computer Science");
        student1.setBatchYear(2027);
        student1.setCgpa(8.92);
        student1.setTargetRole("Java Full Stack Engineer");
        student1.setGithubUsername("ballari-vinuthna");
        student1.setLinkedinUrl("https://linkedin.com/in/ballarivinuthna");
        student1.setPortfolioUrl("https://vinuthna.dev");
        student1.setCareerReadinessScore(84);
        student1.setTechnicalSkillsScore(88);
        student1.setProjectEvidenceScore(91);
        student1.setInterviewReadinessScore(76);
        student1.setExperienceScore(65);
        student1 = studentProfileRepository.save(student1);

        // Student 2: Alex Rivera
        User student2User = userRepository.save(new User("student2@smarthire.ai", defaultPassword, "Alex Rivera", Role.ROLE_STUDENT));
        StudentProfile student2 = new StudentProfile(student2User);
        student2.setHeadline("Frontend & Web Performance Engineer");
        student2.setUniversity("Indian Institute of Information Technology");
        student2.setBatchYear(2027);
        student2.setCgpa(8.40);
        student2.setTargetRole("React Developer");
        student2.setCareerReadinessScore(68);
        studentProfileRepository.save(student2);

        // Student 3: Priya Sharma
        User student3User = userRepository.save(new User("student3@smarthire.ai", defaultPassword, "Priya Sharma", Role.ROLE_STUDENT));
        StudentProfile student3 = new StudentProfile(student3User);
        student3.setHeadline("Cloud Infrastructure & DevOps Engineer");
        student3.setUniversity("Birla Institute of Technology");
        student3.setBatchYear(2027);
        student3.setCgpa(8.75);
        student3.setTargetRole("Software Engineer");
        student3.setCareerReadinessScore(74);
        studentProfileRepository.save(student3);

        // Recruiter
        User recruiterUser = userRepository.save(new User("recruiter@google.com", defaultPassword, "Sarah Vance", Role.ROLE_RECRUITER));
        recruiterProfileRepository.save(new RecruiterProfile(recruiterUser, apex, "Lead Technical Recruiter"));

        // Placement Officer
        User placementUser = userRepository.save(new User("placement@university.edu", defaultPassword, "Dr. K. Ramanathan", Role.ROLE_PLACEMENT_OFFICER));

        // Admin
        User adminUser = userRepository.save(new User("admin@smarthire.ai", defaultPassword, "SmartHire System Admin", Role.ROLE_ADMIN));

        // 4. Proof-of-Skill for Ballari
        StudentSkill sJava = new StudentSkill(student1, java, 90);
        sJava.setAssessmentScore(82);
        sJava.setProjectEvidenceScore(91);
        sJava.setGitHubEvidenceScore(78);
        sJava.setInterviewScore(74);
        sJava.calculateEvidenceStrength();
        studentSkillRepository.save(sJava);

        skillEvidenceRepository.save(new SkillEvidence(sJava, "SmartHire Backend Core Service", "GITHUB", "https://github.com/ballari-vinuthna/smart-hire-core", "Spring Boot multi-module microservice with JWT authentication and connection pooling.", 92));
        skillEvidenceRepository.save(new SkillEvidence(sJava, "Advanced Java Adaptive Assessment", "ASSESSMENT", "", "Scored 82% in JVM Memory & Concurrency adaptive test.", 85));

        StudentSkill sSpring = new StudentSkill(student1, springBoot, 88);
        sSpring.setAssessmentScore(80);
        sSpring.setProjectEvidenceScore(92);
        sSpring.setGitHubEvidenceScore(85);
        sSpring.setInterviewScore(76);
        sSpring.calculateEvidenceStrength();
        studentSkillRepository.save(sSpring);

        StudentSkill sReact = new StudentSkill(student1, react, 85);
        sReact.setAssessmentScore(78);
        sReact.setProjectEvidenceScore(88);
        sReact.setGitHubEvidenceScore(82);
        sReact.setInterviewScore(72);
        sReact.calculateEvidenceStrength();
        studentSkillRepository.save(sReact);

        StudentSkill sSql = new StudentSkill(student1, sql, 85);
        sSql.setAssessmentScore(84);
        sSql.setProjectEvidenceScore(86);
        sSql.setGitHubEvidenceScore(75);
        sSql.setInterviewScore(78);
        sSql.calculateEvidenceStrength();
        studentSkillRepository.save(sSql);

        StudentSkill sDocker = new StudentSkill(student1, docker, 65);
        sDocker.setAssessmentScore(60);
        sDocker.setProjectEvidenceScore(65);
        sDocker.setGitHubEvidenceScore(62);
        sDocker.setInterviewScore(60);
        sDocker.calculateEvidenceStrength();
        studentSkillRepository.save(sDocker);

        StudentSkill sSystemDesign = new StudentSkill(student1, systemDesign, 60);
        sSystemDesign.setAssessmentScore(58);
        sSystemDesign.setProjectEvidenceScore(62);
        sSystemDesign.setGitHubEvidenceScore(55);
        sSystemDesign.setInterviewScore(65);
        sSystemDesign.calculateEvidenceStrength();
        studentSkillRepository.save(sSystemDesign);

        // 5. Projects for Ballari
        projectRepository.save(new Project(
                student1,
                "Enterprise SmartHire Core Backend",
                "Built resilient microservices architecture in Spring Boot 3 with Spring Security, JWT token exchange, and Hibernate caching. Benchmarked sub-20ms P95 query latencies with connection pooling.",
                "Java, Spring Boot, MySQL, JWT, Docker",
                "https://github.com/ballari-vinuthna/smart-hire-core",
                "High",
                true
        ));
        projectRepository.save(new Project(
                student1,
                "Distributed Event Streaming Pipeline",
                "Engineered pub-sub messaging architecture processing 10,000 synthetic events per second with idempotent consumer groups and guaranteed at-least-once delivery.",
                "Java, Apache Kafka, Redis, PostgreSQL",
                "https://github.com/ballari-vinuthna/stream-engine",
                "High",
                true
        ));
        projectRepository.save(new Project(
                student1,
                "Next-Gen Career Intelligence SPA",
                "Developed Apple-aesthetic responsive user interface utilizing React 18, Framer Motion transitions, Web Speech API speech synthesis, and real-time canvas rendering.",
                "React, TypeScript, CSS Tokens, Framer Motion",
                "https://github.com/ballari-vinuthna/smarthire-ui",
                "High",
                true
        ));

        // 6. Certificates & Education & Experience
        certificateRepository.save(new Certificate(student1, "Oracle Certified Professional: Java SE 17 Developer", "Oracle Corporation", LocalDate.of(2025, 6, 15), "https://oracle.com/verify/OCP17-BV"));
        certificateRepository.save(new Certificate(student1, "AWS Certified Cloud Practitioner", "Amazon Web Services", LocalDate.of(2025, 9, 20), "https://aws.amazon.com/verify/CLF-001"));

        educationRepository.save(new Education(student1, "National Institute of Technology", "B.Tech", "Computer Science & Engineering", 2023, 2027, "8.92 CGPA"));
        
        Experience exp = new Experience();
        exp.setStudent(student1);
        exp.setTitle("Software Engineering Intern");
        exp.setCompany("Tech Accelerate Lab");
        exp.setLocation("Bengaluru, India");
        exp.setStartDate(LocalDate.of(2025, 5, 1));
        exp.setEndDate(LocalDate.of(2025, 8, 15));
        exp.setIsCurrent(false);
        exp.setDescription("Architected Spring Boot microservices for asynchronous payload validation. Designed scalable database schema reducing query overhead by 35%.");
        experienceRepository.save(exp);

        // 7. Resumes
        String sampleResumeJson = """
        {
            "fullName": "Ballari Vinuthna",
            "title": "Full Stack Java Engineer",
            "email": "student@smarthire.ai",
            "phone": "+91 98765 43210",
            "location": "Bengaluru, India",
            "summary": "Results-driven Full Stack Java Engineer with demonstrated expertise in Spring Boot microservices, high-throughput REST APIs, and modern React SPAs. Solid foundations in system design, distributed data stores, and containerization.",
            "skills": "Java 17, Spring Boot, Spring Security, Hibernate JPA, React, TypeScript, SQL, Docker, Git, REST APIs, Microservices, Redis",
            "experience": [
                {
                    "title": "Software Engineering Fellow",
                    "company": "Tech Accelerate Lab",
                    "duration": "May 2025 - Aug 2025",
                    "description": "Architected Spring Boot microservices for asynchronous payload validation. Designed scalable database schema reducing query overhead by 35%."
                }
            ],
            "projects": [
                {
                    "name": "SmartHire Career Readiness Core",
                    "tech": "Java, Spring Boot, MySQL, JWT, Docker",
                    "description": "Built resilient backend services with JWT authentication and Apache PDFBox parsing. Containerized services with Docker multi-stage builds."
                },
                {
                    "name": "Distributed Stream Processing Engine",
                    "tech": "Java 17, Apache Kafka, Redis, PostgreSQL",
                    "description": "Implemented high-throughput event listener handling 10k messages/sec with idempotent state reconciliation."
                }
            ],
            "education": [
                {
                    "school": "National Institute of Technology",
                    "degree": "B.Tech in Computer Science & Engineering",
                    "year": "2023 - 2027",
                    "grade": "8.92 CGPA"
                }
            ]
        }
        """;

        Resume primaryResume = resumeRepository.save(new Resume(student1, "Java Full Stack Developer Resume (ATS 84)", "Modern", true, sampleResumeJson));
        resumeVersionRepository.save(new ResumeVersion(primaryResume, 1, sampleResumeJson));

        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setResume(primaryResume);
        analysis.setAtsScore(84);
        analysis.setKeywordsScore(91);
        analysis.setStructureScore(87);
        analysis.setFormattingScore(82);
        analysis.setSkillsScore(89);
        analysis.setReadabilityScore(86);
        analysis.setMatchedKeywords("Java, Spring Boot, REST APIs, Microservices, React, SQL, Docker, Git, JWT");
        analysis.setMissingKeywords("Kubernetes, AWS ECS, Kafka");
        analysis.setSuggestionsJson("Include Kubernetes and cloud orchestration to target senior backend positions | Enhance bullet points with quantifiable metrics");
        resumeAnalysisRepository.save(analysis);

        // 8. Job Twins
        jobTwinRepository.save(new JobTwin(
                "Java Full Stack Engineer",
                "High-performance engineering role demanding enterprise Spring Boot microservices, clean SQL indexing, reactive modern frontends, and container orchestration.",
                "{\"Java\":95,\"Spring Boot\":90,\"React\":82,\"SQL\":85,\"Docker\":65,\"System Design\":60}",
                "1-3 years or equivalent capstone project evidence",
                95
        ));

        jobTwinRepository.save(new JobTwin(
                "React Developer",
                "Frontend engineering specialist driving web performance, reactive state architecture, and accessible Apple-grade UI experiences.",
                "{\"React\":95,\"TypeScript\":90,\"JavaScript\":95,\"HTML/CSS\":92,\"Git\":88,\"System Design\":65}",
                "1-2 years hands-on SPA delivery",
                90
        ));

        // 9. Jobs
        Job job1 = new Job();
        job1.setCompany(apex);
        job1.setTitle("Java Backend Engineer");
        job1.setLocation("Bengaluru / Hybrid");
        job1.setWorkType("Hybrid");
        job1.setSalaryRange("18 - 24 LPA");
        job1.setExperienceRequired("0-2 Years");
        job1.setDeadline(LocalDate.now().plusDays(30));
        job1.setStatus("ACTIVE");
        job1.setMinReadinessScore(80);
        job1.setSkillsRequired("Java, Spring Boot, SQL, Docker");
        job1.setDescription("We are seeking an ambitious Java Backend Engineer to design, deploy, and operate high-scale microservices powering next-generation cloud infrastructure.");
        job1.setRequirements("Strong proficiency in Java 17+, Spring Boot, JPA/Hibernate. Familiarity with Docker and relational SQL optimization.");
        jobRepository.save(job1);

        Job job2 = new Job();
        job2.setCompany(google);
        job2.setTitle("Full Stack Software Engineer");
        job2.setLocation("Hyderabad");
        job2.setWorkType("Onsite");
        job2.setSalaryRange("24 - 32 LPA");
        job2.setExperienceRequired("Fresher / 2027 Graduate");
        job2.setDeadline(LocalDate.now().plusDays(45));
        job2.setStatus("ACTIVE");
        job2.setMinReadinessScore(85);
        job2.setSkillsRequired("Java, React, SQL, System Design, AWS");
        job2.setDescription("Join our Core Engineering Group to build mission-critical enterprise systems handling petabyte-scale data flows.");
        job2.setRequirements("Demonstrated mastery of object-oriented design, RESTful APIs, modern frontend architectures, and data structures.");
        jobRepository.save(job2);

        Job job3 = new Job();
        job3.setCompany(stripe);
        job3.setTitle("Backend Platform Engineer");
        job3.setLocation("Remote");
        job3.setWorkType("Remote");
        job3.setSalaryRange("28 - 36 LPA");
        job3.setExperienceRequired("0-2 Years");
        job3.setDeadline(LocalDate.now().plusDays(20));
        job3.setStatus("ACTIVE");
        job3.setMinReadinessScore(82);
        job3.setSkillsRequired("Java, Spring Boot, SQL, Docker, System Design");
        job3.setDescription("Build the economic infrastructure of the internet. Engineer fault-tolerant payment pipelines with 99.999% uptime.");
        job3.setRequirements("Exceptional code quality, concurrency handling, and distributed database understanding.");
        jobRepository.save(job3);

        // 10. Sample Assessments & Questions
        Assessment assessment = assessmentRepository.save(new Assessment("Java Full Stack Adaptive Assessment", "Java", "Adaptive", 20, 10));

        questionRepository.save(new AssessmentQuestion(
                assessment, 1,
                "Which mechanism in Java 17 provides immutable data carriers with concise syntax and transparent accessors?",
                "Class with final fields", "Java Records", "Lombok Value Object", "Static Nested Class",
                "B", "Java Records (introduced in Java 14 and standardized in Java 16) provide compact, transparent immutability."
        ));
        questionRepository.save(new AssessmentQuestion(
                assessment, 2,
                "In Spring Security, what is the primary benefit of storing user credentials in stateless JSON Web Tokens (JWT)?",
                "Automatic encryption of all payload data", "Eliminates need for server-side session state replication across nodes", "Bypasses all CORS validation", "Ensures unlimited expiration by default",
                "B", "Stateless JWT tokens encapsulate claims cryptographically, allowing horizontally scaled nodes to verify auth without centralized session caches."
        ));
        questionRepository.save(new AssessmentQuestion(
                assessment, 3,
                "Which JPA fetching strategy prevents the notorious N+1 query problem when loading parent entities with their children?",
                "FetchType.LAZY alone", "JOIN FETCH or EntityGraph query hints", "System.gc() invocation", "CascadeType.REMOVE",
                "B", "Using JPQL 'JOIN FETCH' or JPA 2.1 EntityGraphs instructs Hibernate to join and initialize associations in a single SQL query."
        ));
        questionRepository.save(new AssessmentQuestion(
                assessment, 4,
                "What is the default isolation level in MySQL InnoDB and how does it prevent non-repeatable reads?",
                "READ UNCOMMITTED", "READ COMMITTED", "REPEATABLE READ using MVCC snapshot isolation", "SERIALIZABLE with table locks",
                "C", "InnoDB defaults to REPEATABLE READ, utilizing Multi-Version Concurrency Control (MVCC) snapshots to ensure consistent reads."
        ));
        questionRepository.save(new AssessmentQuestion(
                assessment, 5,
                "When scaling a microservice horizontally, which pattern addresses cross-cutting concerns like rate limiting, SSL termination, and routing?",
                "Circuit Breaker Pattern", "API Gateway Pattern", "Saga Pattern", "CQRS Pattern",
                "B", "An API Gateway acts as the single unified entry point for clients, handling SSL termination, rate limiting, and request routing."
        ));

        // Sample completed attempt for Ballari
        AssessmentAttempt attempt = new AssessmentAttempt();
        attempt.setStudent(student1);
        attempt.setAssessment(assessment);
        attempt.setScore(82);
        attempt.setTotalQuestions(10);
        attempt.setCorrectAnswers(8);
        attempt.setStatus("COMPLETED");
        attempt.setFoundationLevel("Strong Foundation");
        attemptRepository.save(attempt);

        // 11. Sample AI Interview for Ballari
        Interview interview = new Interview();
        interview.setStudent(student1);
        interview.setTitle("Java Full Stack AI Mock Simulation");
        interview.setTargetRole("Java Full Stack Engineer");
        interview.setStatus("COMPLETED");
        interview.setOverallScore(76);
        interview.setTechnicalScore(80);
        interview.setRelevanceScore(78);
        interview.setCommunicationScore(75);
        interview.setProblemSolvingScore(72);
        interview.setProjectUnderstandingScore(75);
        interview.setFeedback("Articulated JWT token architecture and Spring Security filters effectively. Solid grasp of relational indexing trade-offs.");
        interview = interviewRepository.save(interview);

        InterviewQuestion q1 = interviewQuestionRepository.save(new InterviewQuestion(interview, 1, "Explain how JWT works, including header, payload, signature, and stateless verification.", "JWT Security", ""));
        InterviewResponse r1 = new InterviewResponse();
        r1.setQuestion(q1);
        r1.setTranscript("JWT consists of Header, Payload, and Signature. The signature is computed using HMAC-SHA256 or RSA. The server validates the signature with the secret key without needing a session lookup.");
        r1.setScore(82);
        r1.setAiEvaluation("Accurate structural breakdown and clear articulation of stateless verification benefits.");
        interviewResponseRepository.save(r1);

        // 12. Career Roadmap for Ballari
        CareerRoadmap roadmap = roadmapRepository.save(new CareerRoadmap(student1, "Java Full Stack Engineer"));
        roadmapNodeRepository.save(new RoadmapNode(roadmap, 1, "Core Java & Memory Model", "JVM internals, GC, and Multithreading", "Master Java 17 records, virtual threads, CompletableFuture, and memory leak diagnosis.", "COMPLETED", "12 hours", "Java, Concurrency, JVM"));
        roadmapNodeRepository.save(new RoadmapNode(roadmap, 2, "Spring Boot & JPA Architecture", "Enterprise MVC & Data Persistence", "Build enterprise REST APIs with Spring Data JPA, connection pooling, and optimistic locking.", "COMPLETED", "15 hours", "Spring Boot, Hibernate, SQL"));
        roadmapNodeRepository.save(new RoadmapNode(roadmap, 3, "Security & Microservices", "Stateless Auth & Gateway Routing", "Implement JWT token exchange, Spring Security filters, and Resilience4j fault tolerance.", "IN_PROGRESS", "18 hours", "JWT, Microservices, Security"));
        roadmapNodeRepository.save(new RoadmapNode(roadmap, 4, "Docker & Cloud Deployment", "Containerization & CI/CD Pipelines", "Containerize backend and React SPA using multi-stage Docker builds, deploy on cloud VM.", "UPCOMING", "10 hours", "Docker, CI/CD, AWS"));
        roadmapNodeRepository.save(new RoadmapNode(roadmap, 5, "Distributed System Design", "Scalability, Caching & Message Queues", "Design scalable URL shorteners, distributed rate limiters, and Kafka event streams.", "UPCOMING", "14 hours", "System Design, Redis, Kafka"));
        roadmapNodeRepository.save(new RoadmapNode(roadmap, 6, "AI Interview & Behavioral Mastery", "STAR Technique & Mock Rounds", "Practice top 30 architectural interview scenarios with real-time speech AI evaluations.", "UPCOMING", "8 hours", "Interviewing, Communication"));
        roadmapNodeRepository.save(new RoadmapNode(roadmap, 7, "Target Role Offer: Java Full Stack Engineer", "Campus Drives & Elite Referral Placements", "Achieve 90%+ verified career readiness and secure tier-1 technology placement.", "UPCOMING", "Final Step", "Offer Acceptance"));

        // 13. Daily Missions for Ballari
        missionRepository.save(new CareerMission(student1, "Build a Spring Boot REST API with JWT Auth", "Implement an authenticated endpoint with role-based authorization filter and push code to your repository.", "Backend Architecture", 25, 4, "Spring Boot", 5));
        missionRepository.save(new CareerMission(student1, "Dockerize a Multi-Container Application", "Write an optimized multi-stage Dockerfile and docker-compose configuration for backend and database.", "DevOps & Cloud", 30, 5, "Docker", 8));
        missionRepository.save(new CareerMission(student1, "Implement LRU Cache in Java", "Write a thread-safe Least Recently Used cache using LinkedHashMap or custom doubly linked list with O(1) ops.", "Data Structures", 20, 3, "DSA", 5));
        missionRepository.save(new CareerMission(student1, "Simulate AI Mock Interview Question on Microservices", "Explain service discovery, API gateway patterns, and distributed tracing in 90 seconds.", "Interview Mastery", 15, 3, "System Design", 4));

        // 14. Campus Placement Drive
        PlacementDrive drive = new PlacementDrive();
        drive.setCompany(apex);
        drive.setTitle("Apex Systems Campus Recruitment Drive 2027");
        drive.setBatchYear(2027);
        drive.setMinCgpa(7.5);
        drive.setMinReadiness(75);
        drive.setDriveDate(LocalDate.now().plusDays(14));
        drive.setLocation("Main Campus Auditorium & Virtual Hackathon");
        drive.setPackageOffered("18 - 24 LPA");
        drive.setStatus("UPCOMING");
        drive.setRolesOffered("Java Full Stack Engineer, Cloud Systems Engineer");
        driveRepository.save(drive);

        // 15. Career Passport for Ballari
        CareerPassport passport = new CareerPassport(student1, "SHX-BV-2027");
        passport.setQrCodeData("https://smarthire.ai/passport/SHX-BV-2027");
        passport.setVerifiedBadgesJson("[\"VERIFIED_FULLSTACK\", \"ATS_84\", \"PROVEN_SPRING_BOOT\", \"AUTHENTICATED_CODE\"]");
        passportRepository.save(passport);

        // 16. Sample Application for Ballari
        Application app = new Application(student1, job1, 84, 92);
        app.setStatus("SHORTLISTED");
        app.setCoverNote("Excited to apply for Java Backend Engineer. My career readiness score is 84% with proven Spring Boot and JPA project evidence.");
        applicationRepository.save(app);

        System.out.println(">>> [SmartHire X] Seeding completed successfully!");
    }
}
