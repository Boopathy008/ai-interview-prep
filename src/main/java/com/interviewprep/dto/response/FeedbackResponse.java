package com.interviewprep.dto.response;
import lombok.*;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class FeedbackResponse {
    private Long id;
    private Double overallScore;
    private String grade;
    private String strengths;
    private String weaknesses;
    private String mistakes;
    private String improvementTips;
    private String studyRecommendations;
    private String detailedFeedback;
    private List<String> nextTopics;
}
