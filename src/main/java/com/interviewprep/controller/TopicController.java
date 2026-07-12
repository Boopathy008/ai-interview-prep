package com.interviewprep.controller;

import com.interviewprep.dto.response.ApiResponse;
import com.interviewprep.entity.Topic;
import com.interviewprep.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicRepository topicRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Topic>>> getAllTopics() {
        return ResponseEntity.ok(ApiResponse.success("Topics loaded", topicRepository.findByIsActiveTrue()));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Topic>>> getByCategory(@PathVariable String category) {
        Topic.Category cat = Topic.Category.valueOf(category.toUpperCase());
        return ResponseEntity.ok(ApiResponse.success("Topics loaded", topicRepository.findByCategoryAndIsActiveTrue(cat)));
    }
}
