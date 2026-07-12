package com.interviewprep.dto.request;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class MockInterviewRequest {
    @NotNull private Long topicId;
    @NotBlank private String language;
    @NotBlank private String difficulty;
    private String targetRole;
    private String companyType;
}
