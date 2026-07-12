package com.interviewprep.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class ChatRequest {
    @NotBlank private String message;
    private String language;
    private String context;
}
