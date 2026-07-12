package com.interviewprep.controller;

import com.interviewprep.dto.request.AnswerSubmitRequest;
import com.interviewprep.dto.request.TestRequest;
import com.interviewprep.dto.response.ApiResponse;
import com.interviewprep.dto.response.TestResponse;
import com.interviewprep.entity.User;
import com.interviewprep.service.TestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
@RequiredArgsConstructor
public class TestController {

    private final TestService testService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TestResponse>> createTest(
            @Valid @RequestBody TestRequest request,
            @AuthenticationPrincipal User user) {
        TestResponse response = testService.createTest(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Test created with AI questions", response));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<TestResponse>> submitAnswers(
            @Valid @RequestBody AnswerSubmitRequest request,
            @AuthenticationPrincipal User user) {
        TestResponse response = testService.submitAnswers(request, user);
        return ResponseEntity.ok(ApiResponse.success("Answers evaluated by AI", response));
    }

    @GetMapping("/{testId}")
    public ResponseEntity<ApiResponse<TestResponse>> getTestResult(
            @PathVariable Long testId,
            @AuthenticationPrincipal User user) {
        TestResponse response = testService.getTestResult(testId, user);
        return ResponseEntity.ok(ApiResponse.success("Test result loaded", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<TestResponse>>> getMyTests(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Tests loaded", testService.getUserTests(user)));
    }
}
