package com.interviewprep.dto.response;
import lombok.*;
import java.time.LocalDateTime;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private Long id;
    private String fullName;
    private String username;
    private String email;
    private String role;
    private String college;
    private String targetRole;
    private String experienceLevel;
    private Integer totalTests;
    private Double totalScore;
    private Double averageScore;
    private Integer streakDays;
    private String avatarUrl;
    private LocalDateTime createdAt;
}
