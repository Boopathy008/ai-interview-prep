package com.interviewprep.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.dto.request.MockInterviewRequest;
import com.interviewprep.entity.*;
import com.interviewprep.exception.ResourceNotFoundException;
import com.interviewprep.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockInterviewService {

    private final MockInterviewRepository mockInterviewRepository;
    private final TopicRepository topicRepository;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    @Transactional
    public Map<String, Object> startMockInterview(MockInterviewRequest request, User user) {
        Topic topic = topicRepository.findById(request.getTopicId())
            .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        MockInterview interview = MockInterview.builder()
            .user(user)
            .topic(topic)
            .language(request.getLanguage())
            .difficulty(Test.Difficulty.valueOf(request.getDifficulty().toUpperCase()))
            .targetRole(request.getTargetRole())
            .companyType(request.getCompanyType() != null ?
                MockInterview.CompanyType.valueOf(request.getCompanyType().toUpperCase()) :
                MockInterview.CompanyType.PRODUCT)
            .status(MockInterview.Status.IN_PROGRESS)
            .interviewTranscript("[]")
            .build();

        interview = mockInterviewRepository.save(interview);

        // Get first question
        String firstQuestion = aiService.generateMockInterviewQuestion(
            request.getLanguage(), topic.getName(), request.getDifficulty(), 1, "");

        return Map.of(
            "interviewId", interview.getId(),
            "questionNumber", 1,
            "aiResponse", parseJsonSafely(firstQuestion),
            "status", "IN_PROGRESS"
        );
    }

    @Transactional
    public Map<String, Object> respondToInterview(Long interviewId, String userAnswer,
                                                   int questionNumber, User user) {
        MockInterview interview = mockInterviewRepository.findById(interviewId)
            .orElseThrow(() -> new ResourceNotFoundException("Interview not found"));

        if (!interview.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized");
        }

        // Get conversation history
        String history = interview.getInterviewTranscript();

        // Get AI evaluation of the response
        String evalJson = aiService.evaluateMockInterviewResponse(
            "Question #" + questionNumber, userAnswer, interview.getLanguage());

        JsonNode evalNode = parseJsonSafely(evalJson);

        // Add to transcript
        List<Map<String, Object>> transcript = new ArrayList<>();
        try {
            JsonNode existingTranscript = objectMapper.readTree(history);
            if (existingTranscript.isArray()) {
                for (JsonNode n : existingTranscript) {
                    transcript.add(objectMapper.convertValue(n, Map.class));
                }
            }
        } catch (Exception e) {
            log.debug("New transcript");
        }

        transcript.add(Map.of(
            "questionNumber", questionNumber,
            "userAnswer", userAnswer,
            "aiEvaluation", evalNode != null ? evalNode.path("feedback").asText("") : "",
            "score", evalNode != null ? evalNode.path("score").asInt(5) : 5
        ));

        try {
            interview.setInterviewTranscript(objectMapper.writeValueAsString(transcript));
        } catch (Exception e) {
            log.error("Transcript serialization failed");
        }

        // Check if should continue
        boolean shouldContinue = questionNumber < 8;
        if (evalNode != null) {
            shouldContinue = !"wrap-up".equals(evalNode.path("nextAction").asText());
            shouldContinue = shouldContinue && questionNumber < 10;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("evaluation", evalNode);

        if (shouldContinue) {
            // Get next question
            String nextQ = aiService.generateMockInterviewQuestion(
                interview.getLanguage(), interview.getTopic().getName(),
                interview.getDifficulty().name(), questionNumber + 1,
                "Previous questions: " + questionNumber + " answered");

            response.put("questionNumber", questionNumber + 1);
            response.put("nextQuestion", parseJsonSafely(nextQ));
            response.put("status", "IN_PROGRESS");
        } else {
            // End interview
            interview.setStatus(MockInterview.Status.COMPLETED);
            interview.setCompletedAt(LocalDateTime.now());

            // Calculate duration
            long minutes = ChronoUnit.MINUTES.between(interview.getStartedAt(), LocalDateTime.now());
            interview.setDuration((int) minutes);

            // Calculate scores
            double avgScore = transcript.stream()
                .mapToInt(t -> (Integer) t.getOrDefault("score", 5))
                .average()
                .orElse(5.0) * 10;

            interview.setOverallScore(avgScore);
            interview.setTechnicalScore(avgScore);
            interview.setCommunicationScore(75.0);
            interview.setProblemSolvingScore(avgScore * 0.9);

            response.put("status", "COMPLETED");
            response.put("finalScore", avgScore);
            response.put("duration", minutes);
        }

        mockInterviewRepository.save(interview);
        return response;
    }

    public List<Map<String, Object>> getUserInterviews(User user) {
        return mockInterviewRepository.findByUserIdOrderByStartedAtDesc(user.getId())
            .stream()
            .map(mi -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", mi.getId());
                map.put("topicName", mi.getTopic().getName());
                map.put("language", mi.getLanguage());
                map.put("difficulty", mi.getDifficulty().name());
                map.put("status", mi.getStatus().name());
                map.put("overallScore", mi.getOverallScore());
                map.put("duration", mi.getDuration());
                map.put("startedAt", mi.getStartedAt());
                map.put("completedAt", mi.getCompletedAt());
                return map;
            })
            .collect(java.util.stream.Collectors.toList());
    }

    private JsonNode parseJsonSafely(String json) {
        try {
            String cleaned = json.trim()
                .replaceAll("```json\\n?", "")
                .replaceAll("```\\n?", "")
                .trim();
            return objectMapper.readTree(cleaned);
        } catch (Exception e) {
            log.error("JSON parse error: {}", e.getMessage());
            return null;
        }
    }
}
