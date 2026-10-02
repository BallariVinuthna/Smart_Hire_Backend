package com.smarthire.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TextChunkService {

    public static class DocumentChunk {
        private final String chunkId;
        private final String documentId;
        private final String filename;
        private final int chunkIndex;
        private final int totalChunks;
        private final int estimatedPage;
        private final String text;
        private final String docType;
        private final String recruiterEmail;

        public DocumentChunk(String documentId, String filename, int chunkIndex, int totalChunks,
                             int estimatedPage, String text, String docType, String recruiterEmail) {
            this.chunkId = documentId + "_chunk_" + chunkIndex;
            this.documentId = documentId;
            this.filename = filename;
            this.chunkIndex = chunkIndex;
            this.totalChunks = totalChunks;
            this.estimatedPage = estimatedPage;
            this.text = text;
            this.docType = docType;
            this.recruiterEmail = recruiterEmail;
        }

        public String getChunkId() { return chunkId; }
        public String getDocumentId() { return documentId; }
        public String getFilename() { return filename; }
        public int getChunkIndex() { return chunkIndex; }
        public int getTotalChunks() { return totalChunks; }
        public int getEstimatedPage() { return estimatedPage; }
        public String getText() { return text; }
        public String getDocType() { return docType; }
        public String getRecruiterEmail() { return recruiterEmail; }
    }

    private static final int CHUNK_SIZE = 800; // Characters per chunk (~150-200 tokens)
    private static final int CHUNK_OVERLAP = 150; // Overlapping boundary characters

    public List<DocumentChunk> splitText(String text, String documentId, String filename, 
                                         int totalPages, String docType, String recruiterEmail) {
        List<DocumentChunk> chunks = new ArrayList<>();
        if (text == null || text.trim().isEmpty()) {
            return chunks;
        }

        String cleaned = text.trim();
        int textLength = cleaned.length();

        List<String> rawChunks = new ArrayList<>();
        int start = 0;

        while (start < textLength) {
            int end = Math.min(start + CHUNK_SIZE, textLength);

            // Attempt to break at nearest sentence or newline delimiter if not at the very end
            if (end < textLength) {
                int lastPeriod = cleaned.lastIndexOf('.', end);
                int lastNewline = cleaned.lastIndexOf('\n', end);
                int breakPoint = Math.max(lastPeriod, lastNewline);

                if (breakPoint > start + (CHUNK_SIZE / 2)) {
                    end = breakPoint + 1;
                }
            }

            String chunkContent = cleaned.substring(start, end).trim();
            if (!chunkContent.isEmpty()) {
                rawChunks.add(chunkContent);
            }

            if (end >= textLength) {
                break;
            }

            start = Math.max(start + 1, end - CHUNK_OVERLAP);
        }

        int totalCount = rawChunks.size();
        for (int i = 0; i < totalCount; i++) {
            // Rough estimation of which page this chunk came from
            int estPage = (totalPages > 0) ? Math.min(totalPages, Math.max(1, (int) Math.ceil(((double) (i + 1) / totalCount) * totalPages))) : 1;
            chunks.add(new DocumentChunk(
                    documentId,
                    filename,
                    i + 1,
                    totalCount,
                    estPage,
                    rawChunks.get(i),
                    docType,
                    recruiterEmail
            ));
        }

        return chunks;
    }
}
