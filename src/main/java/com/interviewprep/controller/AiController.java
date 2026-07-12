package com.interviewprep.controller;

import com.interviewprep.dto.request.ChatRequest;
import com.interviewprep.dto.response.ApiResponse;
import com.interviewprep.entity.User;
import com.interviewprep.service.AiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<String>> chat(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal User user) {
        String response = aiService.chat(request.getMessage(), request.getLanguage(), request.getContext());
        return ResponseEntity.ok(ApiResponse.success("AI response", response));
    }

    @PostMapping("/code-review")
    public ResponseEntity<ApiResponse<String>> reviewCode(
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal User user) {
        String review = aiService.reviewCode(
            request.get("code"), request.get("language"), request.get("problem"));
        return ResponseEntity.ok(ApiResponse.success("Code review complete", review));
    }
}
