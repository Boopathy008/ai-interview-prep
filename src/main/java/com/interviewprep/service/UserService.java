package com.interviewprep.service;

import com.interviewprep.dto.response.UserResponse;
import com.interviewprep.entity.User;
import com.interviewprep.exception.ResourceNotFoundException;
import com.interviewprep.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getProfile(User user) {
        return mapToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse updateTargetRole(User user, String targetRole) {
        user.setTargetRole(targetRole);
        userRepository.save(user);
        return mapToResponse(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private UserResponse mapToResponse(User u) {
        return UserResponse.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .username(u.getUsername())
                .email(u.getEmail())
                .role(u.getRole().name())
                .college(u.getCollege())
                .targetRole(u.getTargetRole())
                .experienceLevel(u.getExperienceLevel().name())
                .totalTests(u.getTotalTests())
                .totalScore(u.getTotalScore())
                .averageScore(u.getAverageScore())
                .streakDays(u.getStreakDays())
                .avatarUrl(u.getAvatarUrl())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
