package com.smarthire.controller;

import com.smarthire.entity.StudentProfile;
import com.smarthire.entity.User;
import com.smarthire.repository.StudentProfileRepository;
import com.smarthire.repository.UserRepository;
import com.smarthire.service.GitHubService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private static final Logger log = LoggerFactory.getLogger(GitHubController.class);

    private final GitHubService gitHubService;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    public GitHubController(
            GitHubService gitHubService,
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository) {
        this.gitHubService = gitHubService;
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, String>> getAuthUrl() {
        String authUrl = gitHubService.getOAuthAuthUrl();
        Map<String, String> resp = new HashMap<>();
        resp.put("authUrl", authUrl);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/callback")
    public ResponseEntity<?> handleOAuthCallback(
            Authentication auth,
            @RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        if (code == null || code.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "OAuth code is required"));
        }

        try {
            String token = gitHubService.exchangeCodeForToken(code);
            Map<String, Object> ghProfile = gitHubService.getAuthenticatedUserProfile(token);
            String username = (String) ghProfile.get("login");

            if (username == null || username.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Could not retrieve GitHub login username"));
            }

            StudentProfile studentProfile = getOrCreateProfile(auth);
            if (studentProfile != null) {
                studentProfile.setGithubUsername(username);
                studentProfile.setGithubAccessToken(token);
                studentProfile.setGithubConnectedAt(LocalDateTime.now());
                studentProfileRepository.save(studentProfile);
            }

            gitHubService.invalidateCache(username);
            Map<String, Object> analytics = gitHubService.getGitHubAnalytics(username, token, true);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Failed to process GitHub OAuth callback", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/connect")
    public ResponseEntity<?> connectUsername(
            Authentication auth,
            @RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "GitHub username is required"));
        }
        username = username.trim();

        try {
            // Verify profile exists on GitHub
            Map<String, Object> profile = gitHubService.getPublicUserProfile(username);
            String verifiedUsername = (String) profile.getOrDefault("login", username);

            StudentProfile studentProfile = getOrCreateProfile(auth);
            if (studentProfile != null) {
                studentProfile.setGithubUsername(verifiedUsername);
                studentProfile.setGithubConnectedAt(LocalDateTime.now());
                studentProfileRepository.save(studentProfile);
            }

            gitHubService.invalidateCache(verifiedUsername);
            Map<String, Object> analytics = gitHubService.getGitHubAnalytics(verifiedUsername, null, true);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            log.error("Failed to connect GitHub account for username: {}", username, e);
            return ResponseEntity.badRequest().body(Map.of("error", "Could not find GitHub account '" + username + "'. Please verify username."));
        }
    }

    @PostMapping("/disconnect")
    public ResponseEntity<?> disconnectGitHub(Authentication auth) {
        StudentProfile profile = getOrCreateProfile(auth);
        if (profile != null) {
            String oldUsername = profile.getGithubUsername();
            profile.setGithubUsername(null);
            profile.setGithubAccessToken(null);
            profile.setGithubConnectedAt(null);
            studentProfileRepository.save(profile);
            gitHubService.invalidateCache(oldUsername);
        }
        return ResponseEntity.ok(Map.of("connected", false, "message", "GitHub account disconnected successfully"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshData(Authentication auth) {
        StudentProfile profile = getOrCreateProfile(auth);
        String username = profile != null ? profile.getGithubUsername() : null;
        String token = profile != null ? profile.getGithubAccessToken() : null;

        if (username == null || username.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No connected GitHub account to refresh"));
        }

        gitHubService.invalidateCache(username);
        Map<String, Object> analytics = gitHubService.getGitHubAnalytics(username, token, true);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(Authentication auth) {
        StudentProfile profile = getOrCreateProfile(auth);
        Map<String, Object> status = new HashMap<>();

        if (profile != null && profile.getGithubUsername() != null && !profile.getGithubUsername().isBlank()) {
            status.put("connected", true);
            status.put("username", profile.getGithubUsername());
            status.put("hasToken", profile.getGithubAccessToken() != null && !profile.getGithubAccessToken().isBlank());
            status.put("connectedAt", profile.getGithubConnectedAt());
        } else {
            status.put("connected", false);
            status.put("username", null);
            status.put("hasToken", false);
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics(
            Authentication auth,
            @RequestParam(required = false) String username,
            @RequestParam(required = false, defaultValue = "false") boolean refresh) {

        String token = null;
        StudentProfile profile = getOrCreateProfile(auth);

        if ((username == null || username.isBlank()) && profile != null) {
            username = profile.getGithubUsername();
            token = profile.getGithubAccessToken();
        }

        if (username == null || username.isBlank()) {
            Map<String, Object> disconnected = new HashMap<>();
            disconnected.put("connected", false);
            disconnected.put("message", "GitHub account is not connected.");
            return ResponseEntity.ok(disconnected);
        }

        Map<String, Object> analytics = gitHubService.getGitHubAnalytics(username, token, refresh);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/repositories")
    public ResponseEntity<?> getRepositories(Authentication auth) {
        ResponseEntity<Map<String, Object>> analyticsResp = getAnalytics(auth, null, false);
        Map<String, Object> analytics = analyticsResp.getBody();
        if (analytics != null && analytics.containsKey("repositories")) {
            return ResponseEntity.ok(analytics.get("repositories"));
        }
        return ResponseEntity.ok(Map.of("repositories", java.util.Collections.emptyList()));
    }

    @GetMapping("/languages")
    public ResponseEntity<?> getLanguages(Authentication auth) {
        ResponseEntity<Map<String, Object>> analyticsResp = getAnalytics(auth, null, false);
        Map<String, Object> analytics = analyticsResp.getBody();
        if (analytics != null && analytics.containsKey("languages")) {
            return ResponseEntity.ok(analytics.get("languages"));
        }
        return ResponseEntity.ok(Map.of("languages", java.util.Collections.emptyMap()));
    }

    @GetMapping("/activity")
    public ResponseEntity<?> getActivity(Authentication auth) {
        ResponseEntity<Map<String, Object>> analyticsResp = getAnalytics(auth, null, false);
        Map<String, Object> analytics = analyticsResp.getBody();
        if (analytics != null) {
            Map<String, Object> act = new HashMap<>();
            act.put("recentActivity", analytics.get("recentActivity"));
            act.put("lastActivityText", analytics.get("lastActivityText"));
            act.put("commitStreakText", analytics.get("commitStreakText"));
            act.put("totalRecentEvents", analytics.get("totalRecentEvents"));
            return ResponseEntity.ok(act);
        }
        return ResponseEntity.ok(Map.of());
    }

    private StudentProfile getOrCreateProfile(Authentication auth) {
        if (auth == null) return null;
        Optional<User> userOpt = userRepository.findByEmail(auth.getName());
        if (userOpt.isEmpty()) return null;
        User user = userOpt.get();
        return studentProfileRepository.findByUser(user)
                .orElseGet(() -> studentProfileRepository.save(new StudentProfile(user)));
    }
}
