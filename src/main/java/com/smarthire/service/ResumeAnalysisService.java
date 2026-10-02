package com.smarthire.service;

import com.smarthire.dto.ResumeDtos.AtsAnalysisResponse;
import com.smarthire.entity.Resume;
import com.smarthire.entity.ResumeAnalysis;
import com.smarthire.entity.StudentProfile;
import com.smarthire.repository.ResumeAnalysisRepository;
import com.smarthire.repository.ResumeRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ResumeTruthService resumeTruthService;

    // Comprehensive catalog of modern high-demand technical skills
    private static final List<String> BENCHMARK_KEYWORDS = List.of(
            "Java", "Spring Boot", "Spring MVC", "REST APIs", "Microservices", "Hibernate", "JPA",
            "SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis", "React", "JavaScript", "TypeScript",
            "Node.js", "Express.js", "HTML5", "CSS3", "Tailwind CSS", "Next.js", "Redux",
            "Docker", "Kubernetes", "AWS", "Azure", "GCP", "Git", "GitHub", "CI/CD",
            "Jenkins", "GitHub Actions", "Python", "Django", "FastAPI", "Flask", "C++",
            "Kafka", "RabbitMQ", "Elasticsearch", "Linux", "System Design", "Data Structures",
            "Algorithms", "OOP", "Maven", "JUnit", "Mockito", "Jest", "Postman",
            "JWT", "OAuth2", "Spring Security", "GraphQL", "Agile", "Scrum"
    );

    private static final List<String> ACTION_VERBS = List.of(
            "engineered", "architected", "developed", "spearheaded", "designed",
            "optimized", "implemented", "automated", "streamlined", "accelerated",
            "deployed", "scaled", "collaborated", "reduced", "increased", "built",
            "created", "led", "managed", "resolved", "maintained", "orchestrated",
            "refactored", "delivered", "integrated", "constructed", "configured"
    );

    public ResumeAnalysisService(
            ResumeRepository resumeRepository,
            ResumeAnalysisRepository resumeAnalysisRepository,
            ResumeTruthService resumeTruthService) {
        this.resumeRepository = resumeRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
        this.resumeTruthService = resumeTruthService;
    }

    @Transactional
    public AtsAnalysisResponse analyzeResume(Long resumeId) {
        Resume resume = null;
        if (resumeId != null) {
            resume = resumeRepository.findById(resumeId).orElse(null);
        }
        if (resume == null) {
            resume = resumeRepository.findAll().stream().findFirst().orElse(null);
        }

        String content = (resume != null && resume.getContentJson() != null) ? resume.getContentJson() : "";
        return performRealAtsAnalysis(resume, content);
    }

    @Transactional
    public AtsAnalysisResponse analyzeUploadedFile(Long resumeId, MultipartFile file) {
        Resume resume = null;
        if (resumeId != null) {
            resume = resumeRepository.findById(resumeId).orElse(null);
        }
        if (resume == null) {
            resume = resumeRepository.findAll().stream().findFirst().orElse(null);
        }

        String extractedText = "";
        try {
            if (file != null && !file.isEmpty()) {
                String filename = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
                if (filename.endsWith(".pdf")) {
                    try (InputStream is = file.getInputStream();
                         PDDocument document = PDDocument.load(is)) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        stripper.setSortByPosition(false);
                        extractedText = stripper.getText(document);
                    }
                } else {
                    extractedText = new String(file.getBytes(), StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            extractedText = "Error extracting text from file: " + e.getMessage();
        }

        if (extractedText == null || extractedText.trim().isEmpty()) {
            extractedText = "No extractable text found in uploaded document.";
        }

        return performRealAtsAnalysis(resume, extractedText);
    }

    private AtsAnalysisResponse performRealAtsAnalysis(Resume resume, String text) {
        String lowerText = text.toLowerCase();
        int wordCount = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;

        // 1. Contact Information Extraction
        Pattern emailPattern = Pattern.compile("(?i)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher emailMatcher = emailPattern.matcher(text);
        String detectedEmail = emailMatcher.find() ? emailMatcher.group() : null;

        Pattern phonePattern = Pattern.compile("(\\+?\\d{1,3}[-.\s]?)?\\(?\\d{3}\\)?[-.\s]?\\d{3}[-.\s]?\\d{4}");
        Matcher phoneMatcher = phonePattern.matcher(text);
        String detectedPhone = phoneMatcher.find() ? phoneMatcher.group() : null;

        boolean hasLinkedIn = lowerText.contains("linkedin.com") || lowerText.contains("linkedin");
        boolean hasGitHub = lowerText.contains("github.com") || lowerText.contains("github");

        // 2. Section Parsing
        List<String> detectedSections = new ArrayList<>();
        if (detectedEmail != null || detectedPhone != null || hasLinkedIn || hasGitHub) {
            detectedSections.add("Contact Information");
        }

        boolean hasEducation = Pattern.compile("(?i)\\b(education|academics?|bachelor|master|b\\.?tech|degree|university|college|gpa)\\b").matcher(lowerText).find();
        if (hasEducation) detectedSections.add("Education");

        boolean hasExperience = Pattern.compile("(?i)\\b(experience|work history|employment|internships?|professional experience)\\b").matcher(lowerText).find();
        if (hasExperience) detectedSections.add("Experience");

        boolean hasProjects = Pattern.compile("(?i)\\b(projects?|capstone|portfolio|academic project)\\b").matcher(lowerText).find();
        if (hasProjects) detectedSections.add("Projects");

        boolean hasSkills = Pattern.compile("(?i)\\b(skills?|technical skills?|technologies|proficiencies|tech stack|competencies)\\b").matcher(lowerText).find();
        if (hasSkills) detectedSections.add("Technical Skills");

        boolean hasSummary = Pattern.compile("(?i)\\b(summary|objective|professional summary|profile|about me)\\b").matcher(lowerText).find();
        if (hasSummary) detectedSections.add("Summary / Objective");

        boolean hasCertifications = Pattern.compile("(?i)\\b(certifications?|certificates?|licenses?|achievements?|awards?)\\b").matcher(lowerText).find();
        if (hasCertifications) detectedSections.add("Certifications / Achievements");

        // 3. Keywords Intelligence
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        for (String kw : BENCHMARK_KEYWORDS) {
            // Match whole word or exact skill pattern
            String pattern = "(?i)\\b" + Pattern.quote(kw) + "\\b";
            if (Pattern.compile(pattern).matcher(lowerText).find() || lowerText.contains(kw.toLowerCase())) {
                matched.add(kw);
            } else {
                missing.add(kw);
            }
        }

        // 4. Action Verbs & Metrics Counting
        int actionVerbCount = 0;
        for (String verb : ACTION_VERBS) {
            Matcher m = Pattern.compile("(?i)\\b" + verb + "\\b").matcher(lowerText);
            while (m.find()) {
                actionVerbCount++;
            }
        }

        int metricsCount = 0;
        Pattern metricPattern = Pattern.compile("(\\d+(\\.\\d+)?%|\\b\\d+k\\b|\\b\\d+x\\b|\\b\\d+\\s*(ms|seconds|users|million|clients|requests)\\b|latency|throughput|reduced|increased|improved|optimized)");
        Matcher metricMatcher = metricPattern.matcher(lowerText);
        while (metricMatcher.find()) {
            metricsCount++;
        }

        // 5. Structure Score (50 - 98)
        int structureScore = 40;
        if (detectedSections.contains("Contact Information")) structureScore += 12;
        if (hasEducation) structureScore += 12;
        if (hasSkills) structureScore += 14;
        if (hasProjects) structureScore += 12;
        if (hasExperience) structureScore += 10;
        if (hasSummary || hasCertifications) structureScore += 5;
        structureScore = Math.min(98, Math.max(45, structureScore));

        // 6. Keywords Score (35 - 98)
        int matchedCount = matched.size();
        int kwScore;
        if (matchedCount >= 14) kwScore = 95;
        else if (matchedCount >= 10) kwScore = 88;
        else if (matchedCount >= 7) kwScore = 80;
        else if (matchedCount >= 5) kwScore = 72;
        else if (matchedCount >= 3) kwScore = 64;
        else if (matchedCount >= 1) kwScore = 52;
        else kwScore = 38;

        // 7. Impact Score (45 - 98)
        int impactScore = Math.min(98, Math.max(45, 50 + (actionVerbCount * 3) + (metricsCount * 4)));

        // 8. Formatting Score (45 - 98)
        int formattingScore = 70;
        if (wordCount >= 350 && wordCount <= 950) formattingScore = 94;
        else if (wordCount >= 200 && wordCount < 350) formattingScore = 82;
        else if (wordCount > 950 && wordCount <= 1300) formattingScore = 80;
        else if (wordCount > 1300) formattingScore = 65;
        else formattingScore = 52;

        if (detectedEmail != null && detectedPhone != null) formattingScore = Math.min(98, formattingScore + 4);

        // 9. Skills Breadth Score (45 - 98)
        boolean hasFrontend = matched.stream().anyMatch(k -> List.of("React", "JavaScript", "TypeScript", "HTML5", "CSS3", "Next.js", "Redux", "Tailwind CSS").contains(k));
        boolean hasBackend = matched.stream().anyMatch(k -> List.of("Java", "Spring Boot", "Python", "Node.js", "Express.js", "Django", "FastAPI", "C++", "REST APIs", "Microservices").contains(k));
        boolean hasDatabase = matched.stream().anyMatch(k -> List.of("SQL", "MySQL", "PostgreSQL", "MongoDB", "Redis", "Hibernate", "JPA").contains(k));
        boolean hasDevops = matched.stream().anyMatch(k -> List.of("Docker", "Kubernetes", "AWS", "Azure", "GCP", "CI/CD", "Git", "GitHub", "Jenkins").contains(k));

        int layers = (hasFrontend ? 1 : 0) + (hasBackend ? 1 : 0) + (hasDatabase ? 1 : 0) + (hasDevops ? 1 : 0);
        int skillsScore = 50 + (layers * 11) + Math.min(10, matchedCount);
        skillsScore = Math.min(98, Math.max(45, skillsScore));

        // 10. Readability Score
        int readabilityScore = Math.min(98, Math.max(45, (int) Math.round((structureScore * 0.45) + (formattingScore * 0.55))));

        // 11. Composite ATS Score (Weighted Enterprise Formula)
        int atsScore = (int) Math.round(
                (kwScore * 0.35) +
                (structureScore * 0.25) +
                (impactScore * 0.15) +
                (formattingScore * 0.15) +
                (skillsScore * 0.10)
        );
        atsScore = Math.min(98, Math.max(35, atsScore));

        // 12. Dynamic Tier Classification
        String overallTier;
        if (atsScore >= 85) {
            overallTier = "Tier-1 Enterprise Ready • High Automated Pass Probability";
        } else if (atsScore >= 72) {
            overallTier = "Competitive Screening Tier • Solid Baseline with Targeted Gaps";
        } else if (atsScore >= 55) {
            overallTier = "Moderate Match • Requires Keyword & Structure Optimization";
        } else {
            overallTier = "Low ATS Compatibility • Immediate Structural Revision Needed";
        }

        // 13. Smart Tailored Suggestions
        List<String> suggestions = new ArrayList<>();
        if (detectedEmail == null || detectedPhone == null) {
            suggestions.add("Ensure both your active professional email address and direct contact phone number are clearly visible at the top.");
        }
        if (!hasLinkedIn) {
            suggestions.add("Add your LinkedIn profile URL in the contact header so recruiters can quickly view endorsements and network credentials.");
        }
        if (!hasGitHub) {
            suggestions.add("Include a link to your GitHub profile or personal portfolio to validate hands-on code quality and projects.");
        }
        if (metricsCount < 3) {
            suggestions.add("Increase quantifiable metrics across your projects (e.g. 'reduced latency by 35%', 'automated build steps saving 4 hrs/week').");
        }
        if (actionVerbCount < 5) {
            suggestions.add("Lead each accomplishment bullet point with assertive action verbs like 'Architected', 'Spearheaded', 'Optimized', or 'Automated'.");
        }
        if (wordCount < 300) {
            suggestions.add("Your resume text is concise (" + wordCount + " words). Expand on your role contributions, technical stack, and architectural decisions.");
        }
        if (!hasExperience && !hasProjects) {
            suggestions.add("Add clear 'Professional Experience' or 'Selected Projects' sections with context, tech stack, and measurable impact.");
        }
        if (!missing.isEmpty()) {
            List<String> topMissing = missing.subList(0, Math.min(4, missing.size()));
            suggestions.add("Incorporate high-value complementary skills into your project descriptions: " + String.join(", ", topMissing) + ".");
        }

        // 14. Evaluate Truth Claims
        StudentProfile student = (resume != null) ? resume.getStudent() : null;
        var truthFindings = resumeTruthService.evaluateClaims(student, text);

        // 15. Persist Analysis (Safely truncate to prevent length overflow)
        if (resume != null) {
            try {
                ResumeAnalysis analysis = new ResumeAnalysis();
                analysis.setResume(resume);
                analysis.setAtsScore(atsScore);
                analysis.setKeywordsScore(kwScore);
                analysis.setStructureScore(structureScore);
                analysis.setFormattingScore(formattingScore);
                analysis.setSkillsScore(skillsScore);
                analysis.setReadabilityScore(readabilityScore);

                String matchedStr = String.join(", ", matched);
                analysis.setMatchedKeywords(matchedStr.length() > 1900 ? matchedStr.substring(0, 1900) : matchedStr);

                String missingStr = String.join(", ", missing);
                analysis.setMissingKeywords(missingStr.length() > 1900 ? missingStr.substring(0, 1900) : missingStr);

                analysis.setSuggestionsJson(String.join(" | ", suggestions));
                resumeAnalysisRepository.save(analysis);
            } catch (Exception ignored) {
                // Keep moving even if saving historical analysis encounters constraint
            }
        }

        // 16. Build Full Response
        AtsAnalysisResponse response = new AtsAnalysisResponse();
        if (resume != null) {
            response.setResumeId(resume.getId());
        }
        response.setAtsScore(atsScore);
        response.setKeywordsScore(kwScore);
        response.setStructureScore(structureScore);
        response.setFormattingScore(formattingScore);
        response.setSkillsScore(skillsScore);
        response.setReadabilityScore(readabilityScore);
        response.setImpactScore(impactScore);
        response.setWordCount(wordCount);
        response.setDetectedEmail(detectedEmail);
        response.setDetectedPhone(detectedPhone);
        response.setOverallTier(overallTier);
        response.setDetectedSections(detectedSections);
        response.setMatchedKeywords(matched);
        response.setMissingKeywords(missing);
        response.setSuggestions(suggestions);
        response.setTruthFindings(truthFindings);

        return response;
    }
}
