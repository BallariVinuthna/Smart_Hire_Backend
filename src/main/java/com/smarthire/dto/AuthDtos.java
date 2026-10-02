package com.smarthire.dto;

import com.smarthire.entity.Role;

public class AuthDtos {

    public static class LoginRequest {
        private String email;
        private String password;

        public LoginRequest() {}
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        private String email;
        private String password;
        private String fullName;
        private Role role = Role.ROLE_STUDENT;
        private String university;
        private Integer batchYear;
        private String targetRole;
        private String companyName;

        public RegisterRequest() {}

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        public String getUniversity() { return university; }
        public void setUniversity(String university) { this.university = university; }
        public Integer getBatchYear() { return batchYear; }
        public void setBatchYear(Integer batchYear) { this.batchYear = batchYear; }
        public String getTargetRole() { return targetRole; }
        public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
        public String getCompanyName() { return companyName; }
        public void setCompanyName(String companyName) { this.companyName = companyName; }
    }

    public static class AuthResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String email;
        private String fullName;
        private String role;
        private Long profileId;

        public AuthResponse() {}

        public AuthResponse(String token, Long id, String email, String fullName, String role, Long profileId) {
            this.token = token;
            this.id = id;
            this.email = email;
            this.fullName = fullName;
            this.role = role;
            this.profileId = profileId;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Long getProfileId() { return profileId; }
        public void setProfileId(Long profileId) { this.profileId = profileId; }
    }
}
