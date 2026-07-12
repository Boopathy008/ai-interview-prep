package com.interviewprep.dto.response;
import lombok.*;
import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private Integer questionNumber;
    private String questionType;
    private String questionText;
    private List<McqOption> options;
    private String language;
    private String difficulty;
    private Integer points;
    // Not sent to user during test; only in results
    private String correctAnswer;
    private String explanation;
    private String userAnswer;
    private Boolean isCorrect;

    @Data @AllArgsConstructor @NoArgsConstructor
    public static class McqOption {
        private String label;
        private String text;
    }
}
