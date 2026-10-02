package com.smarthire.controller;

import com.smarthire.dto.AiDtos.DocumentUploadResponse;
import com.smarthire.entity.RagDocument;
import com.smarthire.service.RagRecruitmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AiDocumentController {

    private final RagRecruitmentService ragService;

    public AiDocumentController(RagRecruitmentService ragService) {
        this.ragService = ragService;
    }

    private String getRecruiterEmail(Authentication auth) {
        if (auth != null && auth.getName() != null && !auth.getName().equalsIgnoreCase("anonymousUser")) {
            return auth.getName();
        }
        return "recruiter@smarthire.ai";
    }

    // =========================================================
    // DOCUMENT INGESTION APIS (Matches both /api/ai and /api/rag)
    // =========================================================

    @PostMapping({"/api/ai/documents/upload", "/api/rag/upload"})
    public ResponseEntity<DocumentUploadResponse> uploadDocument(
            Authentication auth,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "docType", defaultValue = "RESUME") String docType) {

        String recruiterEmail = getRecruiterEmail(auth);
        DocumentUploadResponse response = ragService.processAndIndexDocument(file, docType, recruiterEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/ai/documents")
    public ResponseEntity<List<RagDocument>> getDocuments(Authentication auth) {
        String recruiterEmail = getRecruiterEmail(auth);
        return ResponseEntity.ok(ragService.getDocuments(recruiterEmail));
    }

    @GetMapping("/api/ai/documents/{id}")
    public ResponseEntity<RagDocument> getDocument(@PathVariable Long id) {
        RagDocument doc = ragService.getDocument(id);
        if (doc == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(doc);
    }

    @DeleteMapping("/api/ai/documents/{id}")
    public ResponseEntity<Void> deleteDocument(Authentication auth, @PathVariable Long id) {
        String recruiterEmail = getRecruiterEmail(auth);
        boolean deleted = ragService.deleteDocument(id, recruiterEmail);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
