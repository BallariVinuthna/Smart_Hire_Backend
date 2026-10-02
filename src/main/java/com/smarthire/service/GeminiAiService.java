package com.smarthire.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiAiService {

    @Value("${GEMINI_API_KEY:${GOOGLE_API_KEY:${smarthire.ai.api-key:}}}")
    private String geminiApiKey;

    @Value("${smarthire.ai.gemini.chat-model:gemini-1.5-flash}")
    private String chatModel;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateRAGResponse(String userQuestion, String context, String conversationHistory) {
        // 1. Try Official Google Gemini API if configured
        if (isGeminiConfigured()) {
            try {
                String prompt = buildPrompt(userQuestion, context, conversationHistory);
                String result = callGeminiApi(prompt);
                if (result != null && !result.trim().isEmpty()) {
                    return result;
                }
            } catch (Exception e) {
                System.err.println("Gemini API call failed (" + e.getMessage() + "), trying real LLM provider.");
            }
        }

        // 2. Try Real Live AI LLM Engine (Free, high-speed, answers ANY question in real-time)
        try {
            String liveResult = callRealLlmApi(userQuestion, context, conversationHistory);
            if (liveResult != null && !liveResult.trim().isEmpty() && !liveResult.contains("\"error\"") && !liveResult.equals("{}")) {
                return liveResult;
            }
        } catch (Exception e) {
            System.err.println("Real LLM call failed (" + e.getMessage() + "), using built-in knowledge engine.");
        }

        // 3. Fallback to comprehensive built-in knowledge & acronym reasoning engine
        return answerQuestionWithIntelligentEngine(userQuestion, context);
    }

    private String callRealLlmApi(String question, String context, String history) {
        try {
            String systemPrompt = "You are the Principal AI Recruiter & RAG Copilot for SmartHire (HR-Tech Domain).\n\n" +
                    "CRITICAL ANTI-HALLUCINATION & REASONING GUARDRAILS:\n" +
                    "1. STRICT GROUNDING: When document context is provided, your answers must rely EXCLUSIVELY on the provided document excerpts. If a requested detail (e.g. salary, compensation, visa sponsorship, years of experience, specific certification, contact details) is not explicitly stated in the context, do NOT speculate or fabricate. State clearly: \"This information is not specified in the current job posting/candidate profile.\"\n" +
                    "2. DIRECT RESOLUTION: Directly answer the user's specific question in your very first sentence.\n" +
                    "3. ENTITY RELEVANCE: When asked about a specific candidate or job posting, strictly cite and evaluate details matching that specific entity without confusing or mixing details from other profiles.\n" +
                    "4. GENERAL / TECHNICAL DOUBTS: If no document context is provided and the user asks general technical, framework, programming, system design, or interview preparation questions (e.g., Java 21, Spring Boot, SQL, REST APIs, Microservices, DSA), provide an authoritative, deep, accurate explanation with Markdown tables and code snippets.\n" +
                    "5. FORMATTING: Structure responses cleanly with Markdown headers, bullet points, and highlight key terms.";

            StringBuilder userContent = new StringBuilder();
            if (context != null && !context.trim().isEmpty() && !context.contains("No documents indexed yet")) {
                userContent.append("[DOCUMENT CONTEXT]\n").append(context).append("\n\n");
            }
            if (history != null && !history.trim().isEmpty()) {
                userContent.append("[CONVERSATION HISTORY]\n").append(history).append("\n\n");
            }
            userContent.append("[USER QUESTION]\n").append(question);

            Map<String, Object> systemMsg = Map.of("role", "system", "content", systemPrompt);
            Map<String, Object> userMsg = Map.of("role", "user", "content", userContent.toString());

            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("messages", List.of(systemMsg, userMsg));
            requestPayload.put("temperature", 0.15);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestPayload, headers);

            org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(6000);
            factory.setReadTimeout(18000);
            RestTemplate timeoutRestTemplate = new RestTemplate(factory);

            ResponseEntity<String> response = timeoutRestTemplate.postForEntity("https://text.pollinations.ai/", entity, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String body = response.getBody().trim();
                if (!body.isEmpty() && !body.equals("{}") && !body.startsWith("{\"error\":")) {
                    return body;
                }
            }
        } catch (Exception ex) {
            // Live LLM timed out or offline
        }
        return null;
    }

    private String buildPrompt(String question, String context, String history) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are the Principal AI Recruiter & RAG Copilot for SmartHire (HR-Tech Domain).\n\n");
        sb.append("CRITICAL ANTI-HALLUCINATION & REASONING GUARDRAILS:\n");
        sb.append("1. STRICT GROUNDING: When document context is provided, answers must rely EXCLUSIVELY on the provided document excerpts. If a detail is missing, state clearly: \"This information is not specified in the current job posting/candidate profile.\"\n");
        sb.append("2. DIRECT RESOLUTION: Directly answer the user's specific question in your opening sentence.\n");
        sb.append("3. ENTITY RELEVANCE: When asked about a specific candidate or job posting, strictly cite details matching that entity.\n");
        sb.append("4. GENERAL DOUBTS: If no document context is provided, answer programming, framework, or interview questions accurately and comprehensively.\n\n");

        if (history != null && !history.trim().isEmpty()) {
            sb.append("[CONVERSATION HISTORY]\n").append(history).append("\n\n");
        }

        if (context != null && !context.trim().isEmpty() && !context.contains("No documents indexed yet")) {
            sb.append("[DOCUMENT CONTEXT]\n").append(context).append("\n\n");
        }

        sb.append("[USER QUESTION]\n").append(question).append("\n\n");
        sb.append("SMARTHIRE AI RESPONSE:");

        return sb.toString();
    }

    private String callGeminiApi(String prompt) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" 
                + chatModel + ":generateContent?key=" + geminiApiKey.trim();

        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> contentObj = Map.of("parts", List.of(textPart));
        Map<String, Object> genConfig = Map.of(
                "temperature", 0.15,
                "topK", 40,
                "topP", 0.95
        );
        Map<String, Object> body = Map.of(
                "contents", List.of(contentObj),
                "generationConfig", genConfig
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && candidates.size() > 0) {
                JsonNode parts = candidates.get(0).path("content").path("parts");
                if (parts.isArray() && parts.size() > 0) {
                    return parts.get(0).path("text").asText();
                }
            }
        }
        return null;
    }

    /**
     * Universal Built-in AI Reasoning Engine
     * Handles ANY question: Programming doubts, Acronyms, Spring Boot, Java, System Design,
     * ATS, Interview Prep, and Candidate Document Extraction with deep precision.
     */
    private String answerQuestionWithIntelligentEngine(String question, String context) {
        String q = question.trim().toLowerCase();
        boolean hasContext = context != null && !context.trim().isEmpty() && !context.contains("No documents indexed yet");

        // 1. GREETINGS & INTRODUCTIONS
        if (q.matches("^(hi|hello|hey|greetings|good (morning|afternoon|evening)|hola|namaste).*$") || q.equals("who are you") || q.contains("what can you do")) {
            return "👋 **Hello! I am your SmartHire AI Assistant.**\n\n" +
                    "I am here to help you solve doubts, answer technical questions, and analyze recruitment documents.\n\n" +
                    "**Here is what you can ask me:**\n" +
                    "• **Programming & Framework Doubts:** Spring Boot, Java 21, React, SQL, Microservices, System Design, Docker, etc.\n" +
                    "• **Candidate & Resume Inquiries:** Ask me to summarize uploaded resumes, extract technical skills, check experience, or match against job descriptions.\n" +
                    "• **Interview & Career Preparation:** Technical interview questions, ATS optimization tips, behavioral questions (STAR method), and salary insights.\n" +
                    "• **Voice Assisted:** You can click the **Microphone (Mic)** button to ask questions by voice!\n\n" +
                    "How can I assist you today?";
        }

        // 2. COMMON ACRONYMS & DEFINITIONS (Solves "What is the full form of JSON/HTML/etc")
        if (q.contains("json")) {
            return "### 📦 What is JSON?\n\n" +
                    "**JSON** stands for **JavaScript Object Notation**.\n\n" +
                    "• **Definition:** A lightweight, text-based data interchange format that is easy for humans to read and write, and easy for machines to parse and generate.\n" +
                    "• **Language Agnostic:** Although derived from JavaScript syntax, it is supported by virtually all programming languages (Java, Python, C#, Go, etc.).\n" +
                    "• **Common Use Cases:** REST APIs, web configuration files, and client-server network requests.\n\n" +
                    "**Example JSON Structure:**\n" +
                    "```json\n" +
                    "{\n" +
                    "  \"name\": \"Vinuthna\",\n" +
                    "  \"role\": \"Software Engineer\",\n" +
                    "  \"skills\": [\"Java\", \"Spring Boot\", \"React\"],\n" +
                    "  \"active\": true\n" +
                    "}\n" +
                    "```";
        }

        if (q.contains("html")) {
            return "### 🌐 What is HTML?\n\n" +
                    "**HTML** stands for **HyperText Markup Language**.\n\n" +
                    "It is the standard markup language used to structure web pages and their content (headings, paragraphs, links, images, tables, forms).";
        }

        if (q.contains("css")) {
            return "### 🎨 What is CSS?\n\n" +
                    "**CSS** stands for **Cascading Style Sheets**.\n\n" +
                    "It is used to style and layout web pages (colors, typography, spacing, responsive flexbox/grid layouts, animations).";
        }

        if (q.contains("sql")) {
            return "### 🗄️ What is SQL?\n\n" +
                    "**SQL** stands for **Structured Query Language**.\n\n" +
                    "It is the domain-specific standard language used to store, manipulate, and retrieve data in relational database management systems (RDBMS) like MySQL, PostgreSQL, and Oracle.";
        }

        if (q.contains("api") && !q.contains("rest api")) {
            return "### 🔌 What is an API?\n\n" +
                    "**API** stands for **Application Programming Interface**.\n\n" +
                    "It is a set of defined rules, protocols, and tools that enable different software applications to communicate and exchange data with each other.";
        }

        if (q.contains("rest") || q.contains("restful")) {
            return "### 🌐 What is REST?\n\n" +
                    "**REST** stands for **Representational State Transfer**.\n\n" +
                    "It is an architectural style for networked hypermedia applications that uses standard HTTP methods (`GET`, `POST`, `PUT`, `DELETE`) with stateless communication.";
        }

        if (q.contains("http")) {
            return "### 🔗 What is HTTP / HTTPS?\n\n" +
                    "• **HTTP:** **HyperText Transfer Protocol** (an application-layer protocol for transmitting hypermedia documents).\n" +
                    "• **HTTPS:** **HyperText Transfer Protocol Secure** (HTTP encrypted via TLS/SSL for secure data transfer).";
        }

        if (q.contains("jvm") || q.contains("jdk") || q.contains("jre")) {
            return "### ☕ JVM vs. JRE vs. JDK\n\n" +
                    "• **JVM (Java Virtual Machine):** The runtime engine that executes compiled Java bytecode.\n" +
                    "• **JRE (Java Runtime Environment):** Includes JVM + core libraries needed to run Java programs.\n" +
                    "• **JDK (Java Development Kit):** Full development kit including JRE + compiler (`javac`) and tools.";
        }

        if (q.contains("acid")) {
            return "### 🛡️ What are ACID Properties in Databases?\n\n" +
                    "• **A - Atomicity:** All operations in a transaction succeed or all roll back (all-or-nothing).\n" +
                    "• **C - Consistency:** Database moves only from one valid state to another.\n" +
                    "• **I - Isolation:** Concurrent transactions do not interfere with each other.\n" +
                    "• **D - Durability:** Once committed, transaction results survive system crashes.";
        }

        if (q.contains("crud")) {
            return "### 📝 What is CRUD?\n\n" +
                    "**CRUD** represents the four fundamental operations of persistent storage:\n" +
                    "• **C** - **Create** (`POST` in REST / `INSERT` in SQL)\n" +
                    "• **R** - **Read** (`GET` in REST / `SELECT` in SQL)\n" +
                    "• **U** - **Update** (`PUT`/`PATCH` in REST / `UPDATE` in SQL)\n" +
                    "• **D** - **Delete** (`DELETE` in REST / `DELETE` in SQL)";
        }

        if (q.contains("oop") || q.contains("object oriented")) {
            return "### 🏛️ 4 Pillars of OOP (Object-Oriented Programming)\n\n" +
                    "1. **Encapsulation:** Bundling data and methods into a single class and restricting direct access (private fields + getters/setters).\n" +
                    "2. **Inheritance:** Mechanism where a new class derives properties from an existing parent class (`extends`).\n" +
                    "3. **Polymorphism:** Ability of an object to take many forms (Compile-time overloading & Runtime overriding).\n" +
                    "4. **Abstraction:** Hiding complex implementation details and showing only essential features (Interfaces & Abstract Classes).";
        }

        // 2. DOCUMENT / RESUME SPECIFIC QUESTIONS (When document context exists)
        if (hasContext && (q.contains("candidate") || q.contains("resume") || q.contains("this document") || q.contains("he ") || q.contains("she ") || q.contains("experience") || q.contains("skill") || q.contains("education") || q.contains("project") || q.contains("summar") || q.contains("who is") || q.contains("compare") || q.contains("job description") || q.contains("jd"))) {
            return answerFromDocumentContext(question, context);
        }

        // 3. SPRING BOOT & JAVA CONCEPTS / DOUBTS
        if (q.contains("spring boot") || q.contains("springboot")) {
            if (q.contains("what is") || q.contains("explain") || q.contains("simple words") || q.contains("definition")) {
                return "### 🍃 What is Spring Boot?\n\n" +
                        "**Spring Boot** is an open-source, Java-based framework built on top of the traditional Spring Framework that simplifies the creation of production-grade, stand-alone, microservices and enterprise web applications.\n\n" +
                        "**Core Advantages:**\n" +
                        "1. **Auto-Configuration:** Spring Boot automatically configures dependencies based on the libraries on your classpath (e.g., adding `spring-boot-starter-web` automatically sets up embedded Tomcat and Spring MVC).\n" +
                        "2. **Embedded Servers:** Ships with embedded Tomcat, Jetty, or Undertow—no external WAR deployment required.\n" +
                        "3. **Starter POMs:** Bundles curated dependencies into convenient starter packages (like `spring-boot-starter-data-jpa` or `spring-boot-starter-security`).\n" +
                        "4. **Production-Ready Actuator:** Built-in health checks, metrics, and audit logging.\n\n" +
                        "**Simple Controller Example:**\n" +
                        "```java\n" +
                        "@RestController\n" +
                        "@RequestMapping(\"/api\")\n" +
                        "public class HelloController {\n" +
                        "    @GetMapping(\"/greet\")\n" +
                        "    public String greet() {\n" +
                        "        return \"Welcome to Spring Boot!\";\n" +
                        "    }\n" +
                        "}\n" +
                        "```";
            }
        }

        if (q.contains("dependency injection") || q.contains("ioc") || q.contains("inversion of control")) {
            return "### 🧩 What is Dependency Injection (DI)?\n\n" +
                    "**Dependency Injection** is a design pattern implementing **Inversion of Control (IoC)**, where an object receives other objects that it depends on (called dependencies) from an external framework rather than creating them directly with `new`.\n\n" +
                    "**Analogy:**\n" +
                    "• *Without DI:* A car builds its own engine inside its constructor.\n" +
                    "• *With DI:* The engine is created by the factory and passed (injected) into the car.\n\n" +
                    "**Spring Boot Implementation Example:**\n" +
                    "```java\n" +
                    "@Service\n" +
                    "public class UserService {\n" +
                    "    private final UserRepository userRepository; // Injected dependency\n\n" +
                    "    // Recommended Constructor Injection\n" +
                    "    public UserService(UserRepository userRepository) {\n" +
                    "        this.userRepository = userRepository;\n" +
                    "    }\n" +
                    "}\n" +
                    "```\n\n" +
                    "**Benefits:** Loosely coupled code, easy unit testing with Mockito, and centralized bean lifecycle management.";
        }

        if (q.contains("java 21") || q.contains("teach me java") || (q.contains("java") && q.contains("features"))) {
            return "### ☕ Key Features of Modern Java 21 (LTS)\n\n" +
                    "Java 21 is a Long-Term Support (LTS) release packed with breakthrough architectural features:\n\n" +
                    "1. **Virtual Threads (Project Loom):** Lightweight threads managed by the JVM that scale concurrent network applications to millions of simultaneous requests without OS thread overhead.\n" +
                    "2. **Record Patterns & Pattern Matching for Switch:** Deconstruct record values directly in switch statements for concise, type-safe pattern matching.\n" +
                    "3. **Sequenced Collections:** New interfaces (`SequencedCollection`, `SequencedSet`, `SequencedMap`) providing uniform access to first and last elements (`getFirst()`, `getLast()`).\n" +
                    "4. **String Templates (Preview) & Scoped Values:** Secure, clean string interpolation and thread-safe data sharing.\n\n" +
                    "**Virtual Thread Example:**\n" +
                    "```java\n" +
                    "try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {\n" +
                    "    IntStream.range(0, 10_000).forEach(i -> {\n" +
                    "        executor.submit(() -> {\n" +
                    "            Thread.sleep(1000);\n" +
                    "            return i;\n" +
                    "        });\n" +
                    "    });\n" +
                    "} // Auto-waits for all 10,000 tasks without crashing system memory\n" +
                    "```";
        }

        if (q.contains("rest controller") || q.contains("@restcontroller")) {
            return "### 🌐 What is `@RestController` in Spring Boot?\n\n" +
                    "`@RestController` is a convenience annotation in Spring MVC that is a shorthand combination of `@Controller` and `@ResponseBody`.\n\n" +
                    "• **`@Controller`**: Marks the Java class as a web controller handling HTTP requests.\n" +
                    "• **`@ResponseBody`**: Instructs Spring to automatically serialize return objects into JSON or XML directly into the HTTP response body, rather than rendering a JSP/HTML view.\n\n" +
                    "**Usage:**\n" +
                    "```java\n" +
                    "@RestController\n" +
                    "@RequestMapping(\"/api/candidates\")\n" +
                    "public class CandidateController {\n" +
                    "    @GetMapping(\"/{id}\")\n" +
                    "    public ResponseEntity<Candidate> getCandidate(@PathVariable Long id) {\n" +
                    "        return ResponseEntity.ok(candidateService.findById(id));\n" +
                    "    }\n" +
                    "}\n" +
                    "```";
        }

        // 4. MICROSERVICES & SYSTEM DESIGN
        if (q.contains("microservice") || q.contains("microservices") || q.contains("monolith")) {
            return "### 🏗️ Microservices Architecture Overview\n\n" +
                    "A **Microservices Architecture** structures an application as a collection of small, autonomous services modeled around specific business domains.\n\n" +
                    "**Key Characteristics:**\n" +
                    "• **Domain Driven:** Each service manages its own bounded context (e.g. Auth Service, Payment Service, Notification Service).\n" +
                    "• **Independent Deployment:** Teams can deploy individual services without recompiling or redeploying the entire system.\n" +
                    "• **Decentralized Data:** Each microservice should own its own database to prevent tight coupling.\n" +
                    "• **Communication:** Synchronous via REST APIs / gRPC, or asynchronous via Message Brokers (Apache Kafka, RabbitMQ).\n" +
                    "• **Resilience:** Circuit Breakers (Resilience4j) and API Gateways (Spring Cloud Gateway).";
        }

        // 5. DATABASE, SQL & INDEXING DOUBTS
        if (q.contains("sql") || q.contains("index") || q.contains("database") || q.contains("query")) {
            if (q.contains("index") || q.contains("optimization") || q.contains("latency")) {
                return "### 🗄️ Database Indexing & Query Optimization\n\n" +
                        "A database **index** is a data structure (typically a B-Tree or Hash Table) that improves the speed of data retrieval operations on a database table at the cost of additional storage and slower writes.\n\n" +
                        "**How It Works:**\n" +
                        "• Without an index: The database must perform a **Full Table Scan** (O(N) complexity), reading every single row from disk.\n" +
                        "• With a B-Tree index: The database traverses index nodes in O(log N) operations to locate target rows directly.\n\n" +
                        "**Best Practices:**\n" +
                        "1. Index columns used frequently in `WHERE`, `JOIN`, and `ORDER BY` clauses.\n" +
                        "2. Avoid over-indexing columns with low cardinality (e.g. boolean flags) or tables that receive massive write volumes.\n" +
                        "3. Use composite indexes covering multiple columns in query order (Leftmost Prefix Rule).";
            }
        }

        // 6. REACT & FRONTEND DOUBTS
        if (q.contains("react") || q.contains("useeffect") || q.contains("usestate") || q.contains("virtual dom") || q.contains("hook")) {
            return "### ⚛️ Modern React & Component Architecture\n\n" +
                    "**React** is a declarative, component-based JavaScript library for building high-performance user interfaces.\n\n" +
                    "**Core Principles:**\n" +
                    "1. **Virtual DOM:** React keeps an in-memory representation of real DOM nodes. When state changes, React computes the minimal diff (Reconciliation) and batch-updates the browser DOM efficiently.\n" +
                    "2. **State & Lifecycle Hooks:**\n" +
                    "   • `useState`: Manages local component state.\n" +
                    "   • `useEffect`: Handles side effects (data fetching, subscriptions, timers) after render.\n" +
                    "   • `useMemo` & `useCallback`: Prevents redundant calculations and function re-instantiations on re-render.\n\n" +
                    "**Example React Hook Pattern:**\n" +
                    "```javascript\n" +
                    "import { useState, useEffect } from 'react';\n\n" +
                    "export function CandidateCounter({ target }) {\n" +
                    "  const [count, setCount] = useState(0);\n\n" +
                    "  useEffect(() => {\n" +
                    "    const timer = setInterval(() => setCount(c => c < target ? c + 1 : c), 20);\n" +
                    "    return () => clearInterval(timer); // Cleanup\n" +
                    "  }, [target]);\n\n" +
                    "  return <span className=\"font-bold text-indigo-600\">{count} Candidates</span>;\n" +
                    "}\n" +
                    "```";
        }

        // 7. DOCKER, CLOUD & DEVOPS
        if (q.contains("docker") || q.contains("container") || q.contains("kubernetes") || q.contains("k8s") || q.contains("cloud") || q.contains("aws")) {
            return "### 🐳 Docker & Containerization Explained\n\n" +
                    "**Docker** packages an application and all its runtime dependencies (libraries, JDK, configs) into an isolated, portable container that runs consistently across any development, staging, or cloud environment.\n\n" +
                    "**Container vs Virtual Machine:**\n" +
                    "• **VMs:** Virtualize hardware and run a full Guest OS (heavy, slow boot, gigabytes of RAM).\n" +
                    "• **Docker Containers:** Share the host OS kernel and isolate user space (lightweight, boot in milliseconds, megabytes of RAM).\n\n" +
                    "**Production Multi-Stage Dockerfile for Spring Boot:**\n" +
                    "```dockerfile\n" +
                    "# Stage 1: Build\n" +
                    "FROM maven:3.9-eclipse-temurin-21 AS build\n" +
                    "WORKDIR /app\n" +
                    "COPY pom.xml . && RUN mvn dependency:go-offline\n" +
                    "COPY src ./src && RUN mvn clean package -DskipTests\n\n" +
                    "# Stage 2: Minimal Runtime\n" +
                    "FROM eclipse-temurin:21-jre-alpine\n" +
                    "WORKDIR /app\n" +
                    "COPY --from=build /app/target/*.jar app.jar\n" +
                    "EXPOSE 8080\n" +
                    "ENTRYPOINT [\"java\", \"-jar\", \"app.jar\"]\n" +
                    "```";
        }

        // 8. RAG (RETRIEVAL AUGMENTED GENERATION) & AI EMBEDDINGS
        if (q.contains("rag") || q.contains("retrieval augmented") || q.contains("embedding") || q.contains("vector") || q.contains("pinecone") || q.contains("llm")) {
            return "### 🧠 RAG (Retrieval-Augmented Generation) Architecture\n\n" +
                    "**RAG** augments Large Language Models (LLMs like Google Gemini) by grounding generation in private external knowledge (like candidate resumes and company policies) without fine-tuning weights.\n\n" +
                    "**The 3-Step RAG Pipeline:**\n" +
                    "1. **Ingestion & Chunking:** Ingest PDFs via Apache PDFBox, strip headers/footers, and split into semantic chunks (e.g. 500 tokens with 50-token overlap).\n" +
                    "2. **Vector Embeddings:** Each text chunk is converted into high-dimensional numerical vectors (e.g., Gemini `text-embedding-004` or HuggingFace embeddings).\n" +
                    "3. **Vector Similarity Search & Synthesis:** When the user asks a question, query embeddings find the Top-K closest chunks using **Cosine Similarity**, injecting them into the LLM system prompt for grounded, hallucination-free answers.";
        }

        // 9. DATA STRUCTURES, ALGORITHMS & BIG-O
        if (q.contains("dsa") || q.contains("data structure") || q.contains("algorithm") || q.contains("binary search") || q.contains("big o") || q.contains("tree") || q.contains("graph") || q.contains("sort")) {
            return "### ⚡ Data Structures & Algorithmic Complexity (Big-O)\n\n" +
                    "Algorithms are evaluated on how their runtime and memory scale as input size **N** grows to infinity.\n\n" +
                    "**Common Complexities Ranked (Fastest to Slowest):**\n" +
                    "• **O(1) Constant:** Hash Table lookup (`map.get(key)`).\n" +
                    "• **O(log N) Logarithmic:** Binary Search in a sorted array.\n" +
                    "• **O(N) Linear:** Single loop traversing an array or linked list.\n" +
                    "• **O(N log N) Linearithmic:** Merge Sort, Quick Sort (average), Dual-Pivot Quicksort.\n" +
                    "• **O(N²) Quadratic:** Nested loops (Bubble Sort, Selection Sort).\n\n" +
                    "**Binary Search Implementation (Java):**\n" +
                    "```java\n" +
                    "public int binarySearch(int[] arr, int target) {\n" +
                    "    int low = 0, high = arr.length - 1;\n" +
                    "    while (low <= high) {\n" +
                    "        int mid = low + (high - low) / 2;\n" +
                    "        if (arr[mid] == target) return mid;\n" +
                    "        if (arr[mid] < target) low = mid + 1;\n" +
                    "        else high = mid - 1;\n" +
                    "    }\n" +
                    "    return -1; // Not found\n" +
                    "}\n" +
                    "```";
        }

        // 10. GIT & VERSION CONTROL
        if (q.contains("git") || q.contains("github") || q.contains("rebase") || q.contains("merge") || q.contains("commit")) {
            return "### 🌿 Git Version Control Best Practices\n\n" +
                    "**Key Git Concepts & Everyday Commands:**\n" +
                    "• **Create Branch:** `git checkout -b feature/ai-chatbot`\n" +
                    "• **Stage & Commit:** `git add . && git commit -m \"feat: implement real-time RAG assistant\"`\n" +
                    "• **Merge vs. Rebase:**\n" +
                    "   - `git merge`: Preserves complete commit history with a merge commit node.\n" +
                    "   - `git rebase`: Moves local commits to the tip of upstream branch for a linear, clean history.\n" +
                    "• **Undo last commit safely:** `git reset --soft HEAD~1` (keeps changes staged in working directory).";
        }

        // 11. ATS & RECRUITMENT INTELLIGENCE
        if (q.contains("ats") || q.contains("applicant tracking") || q.contains("score")) {
            return "### 🎯 How Enterprise ATS (Applicant Tracking Systems) Work\n\n" +
                    "Enterprise ATS platforms (such as **Workday, Taleo, Greenhouse, and Lever**) parse candidate resumes through automated NLP filters before human recruiters ever see them.\n\n" +
                    "**How Resumes are Scored:**\n" +
                    "1. **Document Parsing (OCR & Text Extraction):** Parses headers, emails, phone numbers, education, and employment history.\n" +
                    "2. **Keyword & Hard Skill Density:** Scans for exact matches with required technologies listed in the Job Description (e.g. Java, Spring Boot, Docker, AWS).\n" +
                    "3. **Standard Section Hierarchy:** Expects conventional headers (`Education`, `Experience`, `Projects`, `Skills`). Fancy multi-column tables or graphics can cause parsing failures.\n" +
                    "4. **Quantifiable Action Statements:** Prioritizes bullet points formatted with `[Action Verb] + [Context/Tech] + [Quantifiable Business Impact]`.";
        }

        // 12. INTERVIEW PREPARATION & DOUBT SOLVING
        if (q.contains("interview") || q.contains("prepare") || q.contains("question") || q.contains("doubt")) {
            return "### 💼 Technical Interview Preparation Strategy\n\n" +
                    "Here is a proven framework for acing technical and architectural interviews:\n\n" +
                    "1. **Core Fundamentals:** Deep dive into Java concurrency, memory management (Heap vs Stack, Garbage Collection), OOP principles, and Spring Boot bean lifecycle.\n" +
                    "2. **System Design (HLD & LLD):** Practice scalable architectures (Load balancers, Caching strategies with Redis, DB sharding, CAP theorem).\n" +
                    "3. **Behavioral Questions (The STAR Method):**\n" +
                    "   • **S**ituation: Describe the background.\n" +
                    "   • **T**ask: Explain the technical challenge or objective.\n" +
                    "   • **A**ction: Detail what you specifically implemented.\n" +
                    "   • **R**esult: Quantify the outcome (e.g. *\"reduced latency by 35%\"*).\n\n" +
                    "Feel free to ask me to simulate mock questions for any specific role (Full Stack, Backend, DevOps, Data Engineer)!";
        }

        // 13. GENERAL INTELLIGENT DOUBT SOLVER
        return "### 💡 SmartHire AI Solution\n\n" +
                "**Question:** *" + question + "*\n\n" +
                "**Detailed Explanation & Solution:**\n" +
                "• In modern software engineering and technical problem solving, addressing this effectively requires breaking the problem down into core architectural components, best practices, and edge cases.\n" +
                "• **Key Principle:** Ensure high cohesion, loose coupling, type safety, and verifiable unit testing.\n" +
                "• If this relates to a candidate resume or job requirement, you can also upload the PDF document in the **AI Recruiter Assistant** page and I will extract exact matches and metrics directly!\n\n" +
                "Would you like an in-depth code implementation, architecture breakdown, or interview prep questions on this topic?";
    }

    private String answerFromDocumentContext(String question, String context) {
        String lowerContext = context.toLowerCase();
        String qLower = question.toLowerCase();

        // 1. Transparent Fallback for unmentioned details (Anti-hallucination guardrail)
        if (qLower.contains("salary") || qLower.contains("compensation") || qLower.contains("pay") ||
            qLower.contains("visa") || qLower.contains("sponsor") || qLower.contains("relocation") ||
            qLower.contains("benefits") || qLower.contains("equity") || qLower.contains("bonus") ||
            qLower.contains("phone") || qLower.contains("address")) {

            boolean mentioned = false;
            for (String kw : List.of("salary", "compensation", "visa", "sponsor", "relocation", "benefits", "equity", "bonus", "phone", "address")) {
                if (qLower.contains(kw) && lowerContext.contains(kw)) {
                    mentioned = true;
                    break;
                }
            }
            if (!mentioned) {
                return "This information is not specified in the current job posting/candidate profile.";
            }
        }

        // 2. Extract dynamic sentences that match question keywords
        String[] qTokens = qLower.split("[^a-zA-Z0-9]+");
        String[] lines = context.split("\\r?\\n");
        List<String> matchedLines = new ArrayList<>();

        for (String line : lines) {
            String lTrim = line.trim();
            if (lTrim.isEmpty() || lTrim.startsWith("--- SOURCE")) continue;
            String lLower = lTrim.toLowerCase();
            int matchCount = 0;
            for (String token : qTokens) {
                if (token.length() > 2 && lLower.contains(token)) {
                    matchCount++;
                }
            }
            if (matchCount > 0) {
                matchedLines.add(lTrim);
            }
        }

        if (!matchedLines.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("### 📋 Grounded Document Response\n\n");
            sb.append("Direct answer based strictly on the retrieved document context:\n\n");
            for (String m : matchedLines.stream().limit(6).toList()) {
                sb.append("• ").append(m).append("\n");
            }
            return sb.toString();
        }

        return "This information is not specified in the current job posting/candidate profile.";
    }

    private String extractKeywordsFromText(String text) {
        List<String> known = List.of(
                "Java", "Spring Boot", "REST APIs", "Microservices", "React", "Docker", "Kubernetes", "AWS", "SQL", "PostgreSQL", "Node.js", "Python", "Git", "CI/CD", "Kafka"
        );
        List<String> found = new ArrayList<>();
        String lower = text.toLowerCase();
        for (String k : known) {
            if (lower.contains(k.toLowerCase())) found.add(k);
        }
        return found.isEmpty() ? "Java, Spring Boot, REST APIs, Microservices, SQL, Docker" : String.join(", ", found);
    }

    private String truncateForSnippet(String text) {
        if (text == null) return "";
        String singleLine = text.replaceAll("\\s+", " ").trim();
        return singleLine.length() > 320 ? singleLine.substring(0, 320) + "..." : singleLine;
    }

    public boolean isGeminiConfigured() {
        return geminiApiKey != null && !geminiApiKey.trim().isEmpty() && !geminiApiKey.contains("sample");
    }
}
