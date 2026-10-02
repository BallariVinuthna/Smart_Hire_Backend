package com.smarthire.controller;

import com.smarthire.dto.AiDtos.*;
import com.smarthire.service.RagRecruitmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AiChatController {

    private final RagRecruitmentService ragService;

    public AiChatController(RagRecruitmentService ragService) {
        this.ragService = ragService;
    }

    private String getRecruiterEmail(Authentication auth) {
        if (auth != null && auth.getName() != null && !auth.getName().equalsIgnoreCase("anonymousUser")) {
            return auth.getName();
        }
        return "recruiter@smarthire.ai";
    }

    // =========================================================
    // RAG CHAT & CONVERSATIONAL MEMORY APIS
    // =========================================================

    @PostMapping({"/api/ai/chat", "/api/rag/chat"})
    public ResponseEntity<ChatResponse> chat(
            Authentication auth,
            @RequestBody ChatRequest request) {

        String recruiterEmail = getRecruiterEmail(auth);
        ChatResponse response = ragService.chat(request, recruiterEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/ai/conversations")
    public ResponseEntity<List<ConversationSummaryDto>> getConversations(Authentication auth) {
        String recruiterEmail = getRecruiterEmail(auth);
        return ResponseEntity.ok(ragService.getConversations(recruiterEmail));
    }

    @GetMapping("/api/ai/conversations/{id}/messages")
    public ResponseEntity<List<Map<String, Object>>> getConversationMessages(@PathVariable String id) {
        return ResponseEntity.ok(ragService.getConversationMessages(id));
    }

    @DeleteMapping("/api/ai/conversations/{id}")
    public ResponseEntity<Void> deleteConversation(@PathVariable String id) {
        boolean deleted = ragService.deleteConversation(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/api/ai/resume/match")
    public ResponseEntity<ResumeMatchResponse> matchResume(
            Authentication auth,
            @RequestBody ResumeMatchRequest request) {

        String recruiterEmail = getRecruiterEmail(auth);
        ResumeMatchResponse response = ragService.matchResumeToJob(request, recruiterEmail);
        return ResponseEntity.ok(response);
    }
}
