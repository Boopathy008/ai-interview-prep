package com.interviewprep.service;

import com.interviewprep.dto.request.StudyPlanRequest;
import com.interviewprep.entity.*;
import com.interviewprep.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final ProgressRepository progressRepository;
    private final AiService aiService;

    @Transactional
    public Map<String, Object> generateStudyPlan(StudyPlanRequest request, User user) {
        // Get weak topics from progress
        List<Progress> weakProgress = progressRepository.findWeakTopicsByUserId(user.getId())
            .stream().limit(5).collect(Collectors.toList());

        List<String> weakTopics = new ArrayList<>(request.getWeakTopics() != null ?
            request.getWeakTopics() : Collections.emptyList());

        weakProgress.forEach(p ->
            weakTopics.add(p.getTopic().getName() + " (" + p.getLanguage() + ")"));

        String planJson = aiService.generateStudyPlan(
            request.getLanguage(),
            request.getTargetRole() != null ? request.getTargetRole() : user.getTargetRole(),
            request.getDurationWeeks(),
            weakTopics,
            request.getExperienceLevel() != null ?
                request.getExperienceLevel() : user.getExperienceLevel().name()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("planJson", planJson);
        response.put("durationWeeks", request.getDurationWeeks());

        return response;
    }

    @Transactional
    public Map<String, Object> saveStudyPlan(StudyPlanRequest request, User user) {
        // Deactivate old plans
        studyPlanRepository.findByUserIdAndIsActiveTrue(user.getId())
            .ifPresent(old -> {
                old.setIsActive(false);
                studyPlanRepository.save(old);
            });

        StudyPlan plan = StudyPlan.builder()
            .user(user)
            .title(request.getLanguage() + " Interview Preparation Plan - " +
                   request.getDurationWeeks() + " Weeks")
            .planJson(request.getPlanJson())
            .durationWeeks(request.getDurationWeeks())
            .targetRole(request.getTargetRole())
            .build();

        plan = studyPlanRepository.save(plan);

        Map<String, Object> response = new HashMap<>();
        response.put("planId", plan.getId());
        response.put("title", plan.getTitle());
        response.put("planJson", plan.getPlanJson());
        response.put("durationWeeks", plan.getDurationWeeks());
        response.put("createdAt", plan.getCreatedAt());

        return response;
    }

    public Optional<StudyPlan> getActivePlan(User user) {
        return studyPlanRepository.findByUserIdAndIsActiveTrue(user.getId());
    }

    @Transactional
    public void deleteActivePlan(User user) {
        studyPlanRepository.findByUserIdAndIsActiveTrue(user.getId())
            .ifPresent(plan -> {
                plan.setIsActive(false);
                studyPlanRepository.save(plan);
            });
    }

    public List<StudyPlan> getUserPlans(User user) {
        return studyPlanRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }
}
