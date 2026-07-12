package com.interviewprep.controller;

import com.interviewprep.dto.request.MockInterviewRequest;
import com.interviewprep.dto.response.ApiResponse;
import com.interviewprep.entity.User;
import com.interviewprep.service.MockInterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mock-interview")
@RequiredArgsConstructor
public class MockInterviewController {

    private final MockInterviewService mockInterviewService;

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<Map<String, Object>>> startInterview(
            @Valid @RequestBody MockInterviewRequest request,
            @AuthenticationPrincipal User user) {
        Map<String, Object> response = mockInterviewService.startMockInterview(request, user);
        return ResponseEntity.ok(ApiResponse.success("Mock interview started", response));
    }

    @PostMapping("/{interviewId}/respond")
    public ResponseEntity<ApiResponse<Map<String, Object>>> respond(
            @PathVariable Long interviewId,
            @RequestParam String answer,
            @RequestParam int questionNumber,
            @AuthenticationPrincipal User user) {
        Map<String, Object> response = mockInterviewService.respondToInterview(
            interviewId, answer, questionNumber, user);
        return ResponseEntity.ok(ApiResponse.success("Response evaluated", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMyInterviews(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Interviews loaded",
            mockInterviewService.getUserInterviews(user)));
    }
}
