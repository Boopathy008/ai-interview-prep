package com.interviewprep.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class TestRequest {
    @NotNull private Long topicId;
    @NotBlank private String language;
    @NotBlank private String difficulty;
    @NotBlank private String testType;
    private Integer numberOfQuestions = 10;
}
