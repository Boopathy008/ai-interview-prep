package com.interviewprep.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;
@Data
public class AnswerSubmitRequest {
    @NotNull private Long testId;
    @NotNull private Map<Long, String> answers;
    private Integer timeTaken;
}
