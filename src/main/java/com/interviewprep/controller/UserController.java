package com.interviewprep.controller;

import com.interviewprep.dto.response.ApiResponse;
import com.interviewprep.dto.response.UserResponse;
import com.interviewprep.entity.User;
import com.interviewprep.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Profile loaded", userService.getProfile(user)));
    }

    @PatchMapping("/me/target-role")
    public ResponseEntity<ApiResponse<UserResponse>> updateTargetRole(
            @RequestParam String role,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.success("Updated",
                userService.updateTargetRole(user, role)));
    }
}
