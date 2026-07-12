package com.interviewprep.service;

import com.interviewprep.dto.response.*;
import com.interviewprep.entity.*;
import com.interviewprep.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TestRepository testRepository;
    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final AiService aiService;

    public DashboardResponse getDashboard(User user) {
        List<Test> recentTests = testRepository
            .findTop5ByUserIdAndStatusOrderByStartedAtDesc(user.getId(), Test.Status.COMPLETED);

        List<Progress> allProgress = progressRepository.findByUserId(user.getId());
        List<Progress> weakTopics = progressRepository.findWeakTopicsByUserId(user.getId())
            .stream().limit(3).collect(Collectors.toList());

        // Score by topic
        Map<String, Double> scoreByTopic = allProgress.stream()
            .collect(Collectors.toMap(
                p -> p.getTopic().getName() + " (" + p.getLanguage() + ")",
                Progress::getAvgScore,
                (a, b) -> a
            ));

        // Tests by difficulty
        Map<String, Integer> testsByDifficulty = new HashMap<>();
        testsByDifficulty.put("EASY", allProgress.stream().mapToInt(Progress::getEasyCompleted).sum());
        testsByDifficulty.put("MEDIUM", allProgress.stream().mapToInt(Progress::getMediumCompleted).sum());
        testsByDifficulty.put("HARD", allProgress.stream().mapToInt(Progress::getHardCompleted).sum());

        return DashboardResponse.builder()
            .user(mapUserToResponse(user))
            .totalTests(user.getTotalTests())
            .averageScore(user.getAverageScore())
            .streakDays(user.getStreakDays())
            .recentTests(recentTests.stream()
                .map(t -> TestResponse.builder()
                    .id(t.getId())
                    .topicName(t.getTopic().getName())
                    .language(t.getLanguage())
                    .difficulty(t.getDifficulty().name())
                    .testType(t.getTestType().name())
                    .status(t.getStatus().name())
                    .score(t.getScore())
                    .startedAt(t.getStartedAt())
                    .completedAt(t.getCompletedAt())
                    .build())
                .collect(Collectors.toList()))
            .topicProgress(allProgress.stream().map(this::mapProgressToResponse).collect(Collectors.toList()))
            .weakTopics(weakTopics.stream().map(this::mapProgressToResponse).collect(Collectors.toList()))
            .scoreByTopic(scoreByTopic)
            .testsByDifficulty(testsByDifficulty)
            .build();
    }

    public List<ProgressResponse> getUserProgress(User user) {
        return progressRepository.findByUserId(user.getId())
            .stream()
            .map(this::mapProgressToResponse)
            .collect(Collectors.toList());
    }

    public String getWeakTopicAnalysis(User user) {
        List<Progress> progress = progressRepository.findByUserId(user.getId());
        if (progress.isEmpty()) return "{\"message\": \"No data yet. Start taking tests!\"}";

        List<String> topics = progress.stream()
            .map(p -> p.getTopic().getName() + " - " + p.getLanguage())
            .collect(Collectors.toList());
        List<Double> scores = progress.stream()
            .map(Progress::getAvgScore)
            .collect(Collectors.toList());

        return aiService.analyzeWeakTopics(
            progress.get(0).getLanguage(), topics, scores
        );
    }

    // Admin
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalStudents", userRepository.countStudents());
        stats.put("totalTests", testRepository.count());
        stats.put("completedTests", testRepository.countCompletedTests());
        stats.put("recentUsers", userRepository.findAllByOrderByCreatedAtDesc()
            .stream().limit(10).map(this::mapUserToResponse).collect(Collectors.toList()));
        return stats;
    }

    private ProgressResponse mapProgressToResponse(Progress p) {
        return ProgressResponse.builder()
            .id(p.getId())
            .topicName(p.getTopic().getName())
            .topicSlug(p.getTopic().getSlug())
            .language(p.getLanguage())
            .totalTests(p.getTotalTests())
            .avgScore(p.getAvgScore())
            .bestScore(p.getBestScore())
            .easyCompleted(p.getEasyCompleted())
            .mediumCompleted(p.getMediumCompleted())
            .hardCompleted(p.getHardCompleted())
            .masteryLevel(p.getMasteryLevel().name())
            .lastTested(p.getLastTested())
            .build();
    }

    private UserResponse mapUserToResponse(User u) {
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
