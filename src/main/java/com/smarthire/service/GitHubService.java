package com.smarthire.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GitHubService {

    private static final Logger log = LoggerFactory.getLogger(GitHubService.class);

    @Value("${smarthire.github.client-id:}")
    private String clientId;

    @Value("${smarthire.github.client-secret:}")
    private String clientSecret;

    @Value("${smarthire.github.callback-url:http://localhost:5173/student/github}")
    private String callbackUrl;

    private final RestTemplate restTemplate;

    // Cache structure: username -> CacheEntry(data, timestamp)
    private final Map<String, CacheEntry> analyticsCache = new ConcurrentHashMap<>();

    private static class CacheEntry {
        final Map<String, Object> data;
        final long timestamp;

        CacheEntry(Map<String, Object> data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > 15 * 60 * 1000; // 15 mins cache
        }
    }

    public GitHubService() {
        this.restTemplate = new RestTemplate();
    }

    public String getOAuthAuthUrl() {
        if (clientId == null || clientId.isBlank()) {
            return null;
        }
        try {
            String encodedRedirect = URLEncoder.encode(callbackUrl, StandardCharsets.UTF_8);
            return "https://github.com/login/oauth/authorize?client_id=" + clientId +
                    "&redirect_uri=" + encodedRedirect +
                    "&scope=read:user%20repo";
        } catch (Exception e) {
            log.error("Failed to generate OAuth URL", e);
            return null;
        }
    }

    public String exchangeCodeForToken(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("OAuth code must not be empty");
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            Map<String, String> body = new HashMap<>();
            body.put("client_id", clientId);
            body.put("client_secret", clientSecret);
            body.put("code", code);
            body.put("redirect_uri", callbackUrl);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://github.com/login/oauth/access_token",
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map respMap = response.getBody();
                if (respMap.containsKey("access_token")) {
                    return (String) respMap.get("access_token");
                } else if (respMap.containsKey("error")) {
                    throw new RuntimeException("GitHub OAuth error: " + respMap.get("error_description"));
                }
            }
            throw new RuntimeException("Failed to retrieve access token from GitHub");
        } catch (Exception e) {
            log.error("Error exchanging code for access token", e);
            throw new RuntimeException("GitHub authorization exchange failed: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getAuthenticatedUserProfile(String accessToken) {
        try {
            HttpHeaders headers = createAuthHeaders(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            return response.getBody() != null ? (Map<String, Object>) response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("Failed to fetch authenticated user profile", e);
            throw new RuntimeException("Failed to fetch GitHub profile with token: " + e.getMessage());
        }
    }

    public Map<String, Object> getPublicUserProfile(String username) {
        try {
            HttpHeaders headers = createPublicHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://api.github.com/users/" + username,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );
            return response.getBody() != null ? (Map<String, Object>) response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("Failed to fetch public profile for username: {}", username, e);
            throw new RuntimeException("GitHub user '" + username + "' not found or API error: " + e.getMessage());
        }
    }

    public Map<String, Object> getGitHubAnalytics(String username, String accessToken, boolean forceRefresh) {
        if (username == null || username.isBlank()) {
            Map<String, Object> emptyState = new LinkedHashMap<>();
            emptyState.put("connected", false);
            emptyState.put("message", "GitHub account not connected.");
            return emptyState;
        }

        String cacheKey = username.toLowerCase();
        if (!forceRefresh && analyticsCache.containsKey(cacheKey)) {
            CacheEntry entry = analyticsCache.get(cacheKey);
            if (!entry.isExpired()) {
                return entry.data;
            }
        }

        Map<String, Object> analytics = buildRealAnalytics(username, accessToken);
        analyticsCache.put(cacheKey, new CacheEntry(analytics));
        return analytics;
    }

    public void invalidateCache(String username) {
        if (username != null) {
            analyticsCache.remove(username.toLowerCase());
        }
    }

    private Map<String, Object> buildRealAnalytics(String username, String accessToken) {
        Map<String, Object> profile;
        boolean hasToken = accessToken != null && !accessToken.isBlank();
        if (hasToken) {
            try {
                profile = getAuthenticatedUserProfile(accessToken);
                if (profile.containsKey("login")) {
                    username = (String) profile.get("login");
                }
            } catch (Exception e) {
                log.warn("Authenticated fetch failed, falling back to public profile for {}", username);
                profile = getPublicUserProfile(username);
            }
        } else {
            profile = getPublicUserProfile(username);
        }

        List<Map<String, Object>> repositories = fetchAllRepositories(username, accessToken);
        Map<String, Integer> repoTypeStats = calculateRepoTypes(repositories);
        Map<String, Integer> languageDistribution = calculateLanguageDistribution(repositories);
        Map<String, Object> activityMetrics = calculateActivityMetrics(username, accessToken, repositories);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("connected", true);
        result.put("username", profile.getOrDefault("login", username));
        result.put("displayName", profile.getOrDefault("name", username));
        result.put("avatarUrl", profile.get("avatar_url"));
        result.put("htmlUrl", profile.get("html_url"));
        result.put("bio", profile.get("bio"));
        result.put("company", profile.get("company"));
        result.put("location", profile.get("location"));
        result.put("blog", profile.get("blog"));
        result.put("followers", profile.getOrDefault("followers", 0));
        result.put("following", profile.getOrDefault("following", 0));

        // Real Repositories Counts
        int totalRepos = repositories.size();
        if (profile.containsKey("public_repos")) {
            int pubFromProfile = ((Number) profile.get("public_repos")).intValue();
            if (pubFromProfile > totalRepos) {
                totalRepos = pubFromProfile;
            }
        }

        result.put("repositoriesCount", totalRepos);
        result.put("publicRepositoriesCount", repoTypeStats.getOrDefault("public", totalRepos));
        result.put("privateRepositoriesCount", repoTypeStats.getOrDefault("private", 0));
        result.put("forkedRepositoriesCount", repoTypeStats.getOrDefault("forked", 0));
        result.put("originalRepositoriesCount", repoTypeStats.getOrDefault("original", totalRepos));
        result.put("archivedRepositoriesCount", repoTypeStats.getOrDefault("archived", 0));

        // Languages
        result.put("languagesCount", languageDistribution.size());
        result.put("languages", languageDistribution);
        if (!languageDistribution.isEmpty()) {
            Map.Entry<String, Integer> topLang = languageDistribution.entrySet().iterator().next();
            result.put("primaryLanguage", topLang.getKey() + " (" + topLang.getValue() + "%)");
        } else {
            result.put("primaryLanguage", "N/A");
        }

        // Activity
        result.put("recentActivity", activityMetrics.get("recentActivity"));
        result.put("lastActivityText", activityMetrics.get("lastActivityText"));
        result.put("commitStreakText", activityMetrics.get("commitStreakText"));
        result.put("totalRecentEvents", activityMetrics.get("totalRecentEvents"));

        // Normalized Repositories List
        result.put("repositories", repositories);
        result.put("projectsCount", repoTypeStats.getOrDefault("original", totalRepos));

        result.put("lastRefreshedAt", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
        return result;
    }

    private List<Map<String, Object>> fetchAllRepositories(String username, String accessToken) {
        List<Map<String, Object>> allRepos = new ArrayList<>();
        int page = 1;
        int perPage = 100;
        boolean hasMore = true;

        HttpHeaders headers = accessToken != null && !accessToken.isBlank()
                ? createAuthHeaders(accessToken)
                : createPublicHeaders();

        while (hasMore && page <= 5) {
            try {
                String url = (accessToken != null && !accessToken.isBlank())
                        ? "https://api.github.com/user/repos?per_page=" + perPage + "&page=" + page + "&sort=updated"
                        : "https://api.github.com/users/" + username + "/repos?per_page=" + perPage + "&page=" + page + "&sort=updated";

                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, entity, List.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    List<Map<String, Object>> pageRepos = response.getBody();
                    if (pageRepos.isEmpty()) {
                        hasMore = false;
                    } else {
                        for (Map<String, Object> rawRepo : pageRepos) {
                            allRepos.add(normalizeRepository(rawRepo));
                        }
                        if (pageRepos.size() < perPage) {
                            hasMore = false;
                        } else {
                            page++;
                        }
                    }
                } else {
                    hasMore = false;
                }
            } catch (Exception e) {
                log.error("Error fetching repos page {} for username {}", page, username, e);
                hasMore = false;
            }
        }
        return allRepos;
    }

    private Map<String, Object> normalizeRepository(Map<String, Object> raw) {
        Map<String, Object> repo = new LinkedHashMap<>();
        repo.put("name", raw.get("name"));
        repo.put("fullName", raw.get("full_name"));
        repo.put("description", raw.getOrDefault("description", "No description provided."));
        repo.put("htmlUrl", raw.get("html_url"));
        repo.put("language", raw.get("language") != null ? raw.get("language") : "Other");
        repo.put("stars", raw.getOrDefault("stargazers_count", 0));
        repo.put("forks", raw.getOrDefault("forks_count", 0));
        repo.put("isPrivate", raw.getOrDefault("private", false));
        repo.put("isFork", raw.getOrDefault("fork", false));
        repo.put("isArchived", raw.getOrDefault("archived", false));
        repo.put("defaultBranch", raw.getOrDefault("default_branch", "main"));
        repo.put("updatedAt", raw.get("updated_at"));
        repo.put("pushedAt", raw.get("pushed_at"));
        return repo;
    }

    private Map<String, Integer> calculateRepoTypes(List<Map<String, Object>> repos) {
        int publicCount = 0;
        int privateCount = 0;
        int forkedCount = 0;
        int originalCount = 0;
        int archivedCount = 0;

        for (Map<String, Object> r : repos) {
            boolean isPriv = Boolean.TRUE.equals(r.get("isPrivate"));
            boolean isFork = Boolean.TRUE.equals(r.get("isFork"));
            boolean isArch = Boolean.TRUE.equals(r.get("isArchived"));

            if (isPriv) privateCount++;
            else publicCount++;

            if (isFork) forkedCount++;
            else originalCount++;

            if (isArch) archivedCount++;
        }

        Map<String, Integer> stats = new HashMap<>();
        stats.put("public", publicCount);
        stats.put("private", privateCount);
        stats.put("forked", forkedCount);
        stats.put("original", originalCount);
        stats.put("archived", archivedCount);
        return stats;
    }

    private Map<String, Integer> calculateLanguageDistribution(List<Map<String, Object>> repos) {
        Map<String, Integer> counts = new HashMap<>();
        int total = 0;

        for (Map<String, Object> r : repos) {
            String lang = (String) r.get("language");
            if (lang != null && !lang.isBlank() && !"Other".equalsIgnoreCase(lang)) {
                counts.put(lang, counts.getOrDefault(lang, 0) + 1);
                total++;
            }
        }

        if (total == 0) {
            return Collections.emptyMap();
        }

        Map<String, Integer> distribution = new LinkedHashMap<>();
        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int accumulatedPct = 0;
        for (int i = 0; i < sorted.size(); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            int pct = (int) Math.round((entry.getValue() * 100.0) / total);
            if (i == sorted.size() - 1) {
                pct = Math.max(0, 100 - accumulatedPct);
            }
            if (pct > 0) {
                distribution.put(entry.getKey(), pct);
                accumulatedPct += pct;
            }
        }
        return distribution;
    }

    private Map<String, Object> calculateActivityMetrics(String username, String accessToken, List<Map<String, Object>> repos) {
        Map<String, Object> metrics = new HashMap<>();
        Instant newestPush = null;

        for (Map<String, Object> repo : repos) {
            String pushedStr = (String) repo.get("pushedAt");
            if (pushedStr != null) {
                try {
                    Instant inst = Instant.parse(pushedStr);
                    if (newestPush == null || inst.isAfter(newestPush)) {
                        newestPush = inst;
                    }
                } catch (Exception ignored) {}
            }
        }

        int eventCount = 0;
        try {
            HttpHeaders headers = accessToken != null && !accessToken.isBlank()
                    ? createAuthHeaders(accessToken)
                    : createPublicHeaders();

            String eventsUrl = "https://api.github.com/users/" + username + "/events/public?per_page=30";
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<List> resp = restTemplate.exchange(eventsUrl, HttpMethod.GET, entity, List.class);

            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                List events = resp.getBody();
                eventCount = events.size();
            }
        } catch (Exception e) {
            log.debug("Could not fetch events for user {}", username);
        }

        String recentActivity;
        String lastActivityText;
        String commitStreakText;

        if (newestPush != null) {
            long daysAgo = Duration.between(newestPush, Instant.now()).toDays();
            if (daysAgo <= 7) {
                recentActivity = "Active";
                lastActivityText = daysAgo == 0 ? "Today" : daysAgo == 1 ? "Yesterday" : daysAgo + " days ago";
            } else if (daysAgo <= 30) {
                recentActivity = "Moderate";
                lastActivityText = (daysAgo / 7) + " weeks ago";
            } else {
                recentActivity = "Inactive";
                lastActivityText = (daysAgo / 30) + " months ago";
            }
            commitStreakText = eventCount > 0
                    ? "Commit activity verified (" + eventCount + " recent events)"
                    : "Activity derived from repo updates";
        } else {
            recentActivity = "Inactive";
            lastActivityText = "No recent activity";
            commitStreakText = "Contribution streak unavailable";
        }

        metrics.put("recentActivity", recentActivity);
        metrics.put("lastActivityText", lastActivityText);
        metrics.put("commitStreakText", commitStreakText);
        metrics.put("totalRecentEvents", eventCount);
        return metrics;
    }

    private HttpHeaders createPublicHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "SmartHire-App");
        headers.set("Accept", "application/vnd.github+json");
        return headers;
    }

    private HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = createPublicHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
