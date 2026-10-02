package com.smarthire.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

@Service
public class GeminiEmbeddingService {

    @Value("${GEMINI_API_KEY:${GOOGLE_API_KEY:${smarthire.ai.api-key:}}}")
    private String geminiApiKey;

    @Value("${smarthire.ai.gemini.embedding-model:text-embedding-004}")
    private String embeddingModel;

    public static final int EMBEDDING_DIMENSIONS = 768;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public float[] generateEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new float[EMBEDDING_DIMENSIONS];
        }

        if (geminiApiKey != null && !geminiApiKey.trim().isEmpty()) {
            try {
                String url = "https://generativelanguage.googleapis.com/v1beta/models/" 
                        + embeddingModel + ":embedContent?key=" + geminiApiKey.trim();

                Map<String, Object> part = Map.of("text", text);
                Map<String, Object> content = Map.of("parts", List.of(part));
                Map<String, Object> body = Map.of(
                        "model", "models/" + embeddingModel,
                        "content", content
                );

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    JsonNode valuesNode = root.path("embedding").path("values");
                    if (valuesNode.isArray() && valuesNode.size() > 0) {
                        float[] vector = new float[valuesNode.size()];
                        for (int i = 0; i < valuesNode.size(); i++) {
                            vector[i] = (float) valuesNode.get(i).asDouble();
                        }
                        return normalize(vector);
                    }
                }
            } catch (Exception e) {
                System.err.println("Gemini Embedding API call failed (" + e.getMessage() + "), using fallback embedding vector.");
            }
        }

        // Semantic embedding fallback (deterministic 768-dim vector based on semantic terms & hashes)
        return generateDeterministicEmbedding(text);
    }

    public static double cosineSimilarity(float[] vecA, float[] vecB) {
        if (vecA == null || vecB == null || vecA.length == 0 || vecB.length == 0) return 0.0;
        int length = Math.min(vecA.length, vecB.length);
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < length; i++) {
            dotProduct += vecA[i] * vecB[i];
            normA += vecA[i] * vecA[i];
            normB += vecB[i] * vecB[i];
        }
        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private float[] normalize(float[] v) {
        double sumSq = 0.0;
        for (float val : v) sumSq += val * val;
        if (sumSq == 0) return v;
        float norm = (float) Math.sqrt(sumSq);
        float[] res = new float[v.length];
        for (int i = 0; i < v.length; i++) {
            res[i] = v[i] / norm;
        }
        return res;
    }

    private float[] generateDeterministicEmbedding(String text) {
        float[] vector = new float[EMBEDDING_DIMENSIONS];
        try {
            String[] tokens = text.toLowerCase().split("[^a-zA-Z0-9#+]+");
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            for (String token : tokens) {
                if (token.length() < 2) continue;
                byte[] hash = md.digest(token.getBytes(StandardCharsets.UTF_8));
                for (int i = 0; i < hash.length; i++) {
                    int index = Math.abs((hash[i] * 31 + i * 17)) % EMBEDDING_DIMENSIONS;
                    vector[index] += 1.0f;
                }
            }
        } catch (Exception ignored) {}

        return normalize(vector);
    }

    public boolean isGeminiConfigured() {
        return geminiApiKey != null && !geminiApiKey.trim().isEmpty() && !geminiApiKey.contains("sample");
    }
}
