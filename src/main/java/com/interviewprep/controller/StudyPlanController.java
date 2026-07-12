package com.interviewprep.controller;

import com.interviewprep.dto.request.StudyPlanRequest;
import com.interviewprep.dto.response.ApiResponse;
import com.interviewprep.entity.User;
import com.interviewprep.service.StudyPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/study-plan")
@RequiredArgsConstructor
public class StudyPlanController {

    private final StudyPlanService studyPlanService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> generatePlan(
            @Valid @RequestBody StudyPlanRequest request,
            @AuthenticationPrincipal User user) {
        Map<String, Object> plan = studyPlanService.generateStudyPlan(request, user);
        return ResponseEntity.ok(ApiResponse.success("Study plan drafted", plan));
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Map<String, Object>>> savePlan(
            @Valid @RequestBody StudyPlanRequest request,
            @AuthenticationPrincipal User user) {
        Map<String, Object> plan = studyPlanService.saveStudyPlan(request, user);
        return ResponseEntity.ok(ApiResponse.success("Study plan saved", plan));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<?>> getActivePlan(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Plan loaded",
            studyPlanService.getActivePlan(user).orElse(null)));
    }

    @DeleteMapping("/active")
    public ResponseEntity<ApiResponse<?>> deleteActivePlan(@AuthenticationPrincipal User user) {
        studyPlanService.deleteActivePlan(user);
        return ResponseEntity.ok(ApiResponse.success("Study plan deleted", null));
    }
}
