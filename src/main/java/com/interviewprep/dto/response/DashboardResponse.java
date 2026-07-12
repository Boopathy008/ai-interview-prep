package com.interviewprep.dto.response;
import lombok.*;
import java.util.List;
import java.util.Map;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DashboardResponse {
    private UserResponse user;
    private Integer totalTests;
    private Double averageScore;
    private Integer streakDays;
    private Integer rank;
    private List<TestResponse> recentTests;
    private List<ProgressResponse> topicProgress;
    private List<ProgressResponse> weakTopics;
    private Map<String, Double> scoreByTopic;
    private Map<String, Integer> testsByDifficulty;
}
