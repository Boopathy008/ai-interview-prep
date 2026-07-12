package com.interviewprep.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;
@Data
public class StudyPlanRequest {
    @NotBlank private String language;
    private String targetRole;
    private Integer durationWeeks = 4;
    private List<String> weakTopics;
    private String experienceLevel;
    private String planJson;
}
