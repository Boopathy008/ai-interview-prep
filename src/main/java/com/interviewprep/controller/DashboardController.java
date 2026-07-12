package com.interviewprep.controller;

import com.interviewprep.dto.response.ApiResponse;
import com.interviewprep.dto.response.DashboardResponse;
import com.interviewprep.entity.User;
import com.interviewprep.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Dashboard loaded", dashboardService.getDashboard(user)));
    }

    @GetMapping("/progress")
    public ResponseEntity<ApiResponse<?>> getProgress(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Progress loaded", dashboardService.getUserProgress(user)));
    }

    @GetMapping("/weak-topics")
    public ResponseEntity<ApiResponse<String>> getWeakTopics(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Analysis complete", dashboardService.getWeakTopicAnalysis(user)));
    }
}
