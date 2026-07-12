package com.interviewprep.dto.response;
import lombok.*;
import java.time.LocalDate;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ProgressResponse {
    private Long id;
    private String topicName;
    private String topicSlug;
    private String language;
    private Integer totalTests;
    private Double avgScore;
    private Double bestScore;
    private Integer easyCompleted;
    private Integer mediumCompleted;
    private Integer hardCompleted;
    private String masteryLevel;
    private LocalDate lastTested;
}
