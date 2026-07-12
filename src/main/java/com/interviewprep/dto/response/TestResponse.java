package com.interviewprep.dto.response;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TestResponse {
    private Long id;
    private String topicName;
    private String language;
    private String difficulty;
    private String testType;
    private String status;
    private Integer totalQuestions;
    private Integer attemptedQuestions;
    private Integer correctAnswers;
    private Double score;
    private Integer timeTaken;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private List<QuestionResponse> questions;
    private FeedbackResponse feedback;
}
