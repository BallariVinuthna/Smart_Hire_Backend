package com.smarthire.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PineconeVectorService {

    @Value("${PINECONE_API_KEY:}")
    private String pineconeApiKey;

    @Value("${PINECONE_INDEX_NAME:ai-rag-documents}")
    private String indexName;

    @Value("${PINECONE_ENVIRONMENT:us-east-1}")
    private String environment;

    @Value("${PINECONE_HOST:}")
    private String pineconeHost;

    @Value("${PINECONE_NAMESPACE:default}")
    private String namespace;

    private final GeminiEmbeddingService embeddingService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // High-performance resilient in-memory vector store cache for instant recall and offline/demo fallback
    public static class VectorRecord {
        private final String id;
        private final float[] values;
        private final Map<String, Object> metadata;

        public VectorRecord(String id, float[] values, Map<String, Object> metadata) {
            this.id = id;
            this.values = values;
            this.metadata = metadata;
        }

        public String getId() { return id; }
        public float[] getValues() { return values; }
        public Map<String, Object> getMetadata() { return metadata; }
    }

    public static class ScoredChunk {
        private final String chunkId;
        private final String documentId;
        private final String filename;
        private final int chunkIndex;
        private final int estimatedPage;
        private final String text;
        private final String docType;
        private final double similarityScore;

        public ScoredChunk(String chunkId, String documentId, String filename, int chunkIndex, 
                           int estimatedPage, String text, String docType, double similarityScore) {
            this.chunkId = chunkId;
            this.documentId = documentId;
            this.filename = filename;
            this.chunkIndex = chunkIndex;
            this.estimatedPage = estimatedPage;
            this.text = text;
            this.docType = docType;
            this.similarityScore = similarityScore;
        }

        public String getChunkId() { return chunkId; }
        public String getDocumentId() { return documentId; }
        public String getFilename() { return filename; }
        public int getChunkIndex() { return chunkIndex; }
        public int getEstimatedPage() { return estimatedPage; }
        public String getText() { return text; }
        public String getDocType() { return docType; }
        public double getSimilarityScore() { return similarityScore; }
    }

    private final Map<String, VectorRecord> localVectorStore = new ConcurrentHashMap<>();

    public PineconeVectorService(GeminiEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    public boolean upsertChunks(List<TextChunkService.DocumentChunk> chunks) {
        if (chunks == null || chunks.isEmpty()) {
            return true;
        }

        List<VectorRecord> recordsToUpsert = new ArrayList<>();

        for (TextChunkService.DocumentChunk chunk : chunks) {
            float[] vector = embeddingService.generateEmbedding(chunk.getText());

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("documentId", chunk.getDocumentId());
            metadata.put("filename", chunk.getFilename());
            metadata.put("chunkIndex", chunk.getChunkIndex());
            metadata.put("totalChunks", chunk.getTotalChunks());
            metadata.put("estimatedPage", chunk.getEstimatedPage());
            metadata.put("docType", chunk.getDocType() != null ? chunk.getDocType() : "GENERAL");
            metadata.put("recruiterEmail", chunk.getRecruiterEmail() != null ? chunk.getRecruiterEmail() : "default");
            metadata.put("text", chunk.getText());

            VectorRecord record = new VectorRecord(chunk.getChunkId(), vector, metadata);
            recordsToUpsert.add(record);

            // Always update local memory store for redundancy
            localVectorStore.put(chunk.getChunkId(), record);
        }

        // Try upserting to Pinecone if configured
        if (isPineconeConfigured()) {
            try {
                String host = resolvePineconeHost();
                String url = "https://" + host + "/vectors/upsert";

                List<Map<String, Object>> pineconeVectors = new ArrayList<>();
                for (VectorRecord r : recordsToUpsert) {
                    List<Float> valList = new ArrayList<>(r.getValues().length);
                    for (float f : r.getValues()) valList.add(f);

                    Map<String, Object> vecObj = new HashMap<>();
                    vecObj.put("id", r.getId());
                    vecObj.put("values", valList);
                    vecObj.put("metadata", r.getMetadata());
                    pineconeVectors.add(vecObj);
                }

                Map<String, Object> payload = Map.of(
                        "vectors", pineconeVectors,
                        "namespace", namespace != null ? namespace : "default"
                );

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Api-Key", pineconeApiKey.trim());

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    System.out.println("Pinecone: successfully upserted " + chunks.size() + " vectors to index '" + indexName + "'.");
                    return true;
                }
            } catch (Exception e) {
                System.err.println("Pinecone upsert encountered error (" + e.getMessage() + "), indexed into local vector store.");
            }
        }

        return true;
    }

    public int getVectorCount() {
        return localVectorStore.size();
    }

    public List<ScoredChunk> similaritySearch(String query, int topK, String targetDocumentId, String recruiterEmail) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        float[] queryVector = embeddingService.generateEmbedding(query);

        // Try Pinecone if configured
        if (isPineconeConfigured()) {
            try {
                String host = resolvePineconeHost();
                String url = "https://" + host + "/query";

                List<Float> queryValList = new ArrayList<>(queryVector.length);
                for (float f : queryVector) queryValList.add(f);

                Map<String, Object> payload = new HashMap<>();
                payload.put("vector", queryValList);
                payload.put("topK", topK > 0 ? topK : 4);
                payload.put("includeMetadata", true);
                payload.put("namespace", namespace != null ? namespace : "default");

                Map<String, Object> filter = new HashMap<>();
                if (targetDocumentId != null && !targetDocumentId.trim().isEmpty()) {
                    filter.put("documentId", targetDocumentId);
                }
                if (recruiterEmail != null && !recruiterEmail.trim().isEmpty()) {
                    filter.put("recruiterEmail", recruiterEmail);
                }
                if (!filter.isEmpty()) {
                    payload.put("filter", filter);
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Api-Key", pineconeApiKey.trim());

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    JsonNode matches = root.path("matches");

                    if (matches.isArray() && matches.size() > 0) {
                        List<ScoredChunk> pineconeResults = new ArrayList<>();
                        for (JsonNode m : matches) {
                            String id = m.path("id").asText();
                            double score = m.path("score").asDouble(0.0);
                            JsonNode meta = m.path("metadata");
                            String docId = meta.path("documentId").asText("");
                            String fname = meta.path("filename").asText("Document");
                            int cIdx = meta.path("chunkIndex").asInt(1);
                            int page = meta.path("estimatedPage").asInt(1);
                            String text = meta.path("text").asText("");
                            String type = meta.path("docType").asText("GENERAL");

                            pineconeResults.add(new ScoredChunk(id, docId, fname, cIdx, page, text, type, score));
                        }
                        return pineconeResults;
                    }
                }
            } catch (Exception e) {
                System.err.println("Pinecone query failed (" + e.getMessage() + "), falling back to local vector store.");
            }
        }

        // Local Hybrid Cosine Similarity + Keyword Overlap Search
        List<ScoredChunk> localResults = new ArrayList<>();

        for (VectorRecord record : localVectorStore.values()) {
            Map<String, Object> meta = record.getMetadata();

            if (targetDocumentId != null && !targetDocumentId.trim().isEmpty()) {
                if (!targetDocumentId.equals(meta.get("documentId"))) continue;
            }

            if (recruiterEmail != null && !recruiterEmail.trim().isEmpty()) {
                Object docOwner = meta.get("recruiterEmail");
                if (docOwner != null && !docOwner.equals("default") && !recruiterEmail.equalsIgnoreCase(docOwner.toString())) {
                    continue;
                }
            }

            double cosSim = GeminiEmbeddingService.cosineSimilarity(queryVector, record.getValues());
            String chunkText = (String) meta.getOrDefault("text", "");
            double keywordScore = calculateTermOverlap(query, chunkText);
            // Hybrid fusion score: 60% semantic embedding + 40% keyword match
            double sim = (cosSim * 0.6) + (keywordScore * 0.4);

            localResults.add(new ScoredChunk(
                    record.getId(),
                    (String) meta.getOrDefault("documentId", ""),
                    (String) meta.getOrDefault("filename", "Document"),
                    ((Number) meta.getOrDefault("chunkIndex", 1)).intValue(),
                    ((Number) meta.getOrDefault("estimatedPage", 1)).intValue(),
                    chunkText,
                    (String) meta.getOrDefault("docType", "GENERAL"),
                    sim
            ));
        }

        localResults.sort((a, b) -> Double.compare(b.getSimilarityScore(), a.getSimilarityScore()));
        return localResults.stream().limit(Math.max(1, topK)).collect(Collectors.toList());
    }

    private double calculateTermOverlap(String query, String text) {
        if (query == null || text == null || query.isBlank() || text.isBlank()) return 0.0;
        String[] qTokens = query.toLowerCase().split("[^a-zA-Z0-9]+");
        String textLower = text.toLowerCase();
        int matches = 0;
        int total = 0;
        for (String token : qTokens) {
            if (token.length() > 2) {
                total++;
                if (textLower.contains(token)) {
                    matches++;
                }
            }
        }
        return total > 0 ? (double) matches / total : 0.0;
    }

    public void deleteDocumentVectors(String documentId) {
        if (documentId == null) return;

        // Remove from local vector store
        localVectorStore.entrySet().removeIf(e -> documentId.equals(e.getValue().getMetadata().get("documentId")));

        // Remove from Pinecone if configured
        if (isPineconeConfigured()) {
            try {
                String host = resolvePineconeHost();
                String url = "https://" + host + "/vectors/delete";

                Map<String, Object> payload = Map.of(
                        "filter", Map.of("documentId", documentId),
                        "namespace", namespace != null ? namespace : "default"
                );

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Api-Key", pineconeApiKey.trim());

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
                restTemplate.postForEntity(url, request, String.class);
            } catch (Exception e) {
                System.err.println("Pinecone delete failed: " + e.getMessage());
            }
        }
    }

    public boolean isPineconeConfigured() {
        return pineconeApiKey != null && !pineconeApiKey.trim().isEmpty() && !pineconeApiKey.contains("sample");
    }

    private String resolvePineconeHost() {
        if (pineconeHost != null && !pineconeHost.trim().isEmpty()) {
            return pineconeHost.replace("https://", "").replace("http://", "").replaceAll("/$", "");
        }
        // Standard Pinecone Serverless URL structure: {index-name}-{project-id}.svc.{environment}.pinecone.io
        return indexName + ".svc." + environment + ".pinecone.io";
    }
}
