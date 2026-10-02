package com.smarthire.controller;

import com.smarthire.dto.AuthDtos.*;
import com.smarthire.entity.*;
import com.smarthire.repository.*;
import com.smarthire.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            RecruiterProfileRepository recruiterProfileRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.recruiterProfileRepository = recruiterProfileRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long profileId = null;
        if (user.getRole() == Role.ROLE_STUDENT) {
            profileId = studentProfileRepository.findByUserId(user.getId())
                    .map(StudentProfile::getId).orElse(null);
        } else if (user.getRole() == Role.ROLE_RECRUITER) {
            profileId = recruiterProfileRepository.findByUserId(user.getId())
                    .map(RecruiterProfile::getId).orElse(null);
        }

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                profileId
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("{\"error\": \"Email is already registered\"}");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(registerRequest.getRole() != null ? registerRequest.getRole() : Role.ROLE_STUDENT);
        user = userRepository.save(user);

        Long profileId = null;
        if (user.getRole() == Role.ROLE_STUDENT) {
            StudentProfile profile = new StudentProfile(user);
            profile.setUniversity(registerRequest.getUniversity() != null ? registerRequest.getUniversity() : "National Institute of Technology");
            profile.setBatchYear(registerRequest.getBatchYear() != null ? registerRequest.getBatchYear() : 2027);
            profile.setTargetRole(registerRequest.getTargetRole() != null ? registerRequest.getTargetRole() : "Java Full Stack Engineer");
            profile.setCareerReadinessScore(72);
            profile = studentProfileRepository.save(profile);
            profileId = profile.getId();
        } else if (user.getRole() == Role.ROLE_RECRUITER) {
            Company company = companyRepository.findByName(registerRequest.getCompanyName() != null ? registerRequest.getCompanyName() : "TechCorp")
                    .orElseGet(() -> companyRepository.save(new Company(registerRequest.getCompanyName() != null ? registerRequest.getCompanyName() : "TechCorp", "Enterprise Software", "Bengaluru", "https://techcorp.io")));
            RecruiterProfile profile = new RecruiterProfile(user, company, "Technical Recruiter");
            profile = recruiterProfileRepository.save(profile);
            profileId = profile.getId();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerRequest.getEmail(), registerRequest.getPassword())
        );
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new AuthResponse(
                jwt,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                profileId
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("{\"error\": \"Unauthenticated\"}");
        }
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long profileId = null;
        if (user.getRole() == Role.ROLE_STUDENT) {
            profileId = studentProfileRepository.findByUserId(user.getId()).map(StudentProfile::getId).orElse(null);
        } else if (user.getRole() == Role.ROLE_RECRUITER) {
            profileId = recruiterProfileRepository.findByUserId(user.getId()).map(RecruiterProfile::getId).orElse(null);
        }

        return ResponseEntity.ok(new AuthResponse(
                null,
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                profileId
        ));
    }
}
