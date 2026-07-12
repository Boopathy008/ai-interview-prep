package com.interviewprep.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.dto.request.AnswerSubmitRequest;
import com.interviewprep.dto.request.TestRequest;
import com.interviewprep.dto.response.*;
import com.interviewprep.entity.*;
import com.interviewprep.exception.ResourceNotFoundException;
import com.interviewprep.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    private final TestRepository testRepository;
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AiFeedbackRepository aiFeedbackRepository;
    private final ProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final AiService aiService;
    private final ObjectMapper objectMapper;

    // ---------------------------------------------------------------
    // Create Test & Generate Questions via AI
    // ---------------------------------------------------------------
    @Transactional
    public TestResponse createTest(TestRequest request, User user) {
        Topic topic = topicRepository.findById(request.getTopicId())
            .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        Test test = Test.builder()
            .user(user)
            .topic(topic)
            .language(request.getLanguage())
            .difficulty(Test.Difficulty.valueOf(request.getDifficulty().toUpperCase()))
            .testType(Test.TestType.valueOf(request.getTestType().toUpperCase()))
            .status(Test.Status.IN_PROGRESS)
            .build();

        test = testRepository.save(test);

        // Generate questions via AI
        int numQuestions = Math.min(request.getNumberOfQuestions(), 15);
        List<QuestionResponse> aiQuestions = aiService.generateQuestions(
            request.getLanguage(),
            topic.getName(),
            request.getDifficulty(),
            request.getTestType(),
            numQuestions
        );

        // Save questions to DB
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < aiQuestions.size(); i++) {
            QuestionResponse qr = aiQuestions.get(i);
            Question question = Question.builder()
                .test(test)
                .questionNumber(i + 1)
                .questionType(Question.QuestionType.valueOf(
                    request.getTestType().equalsIgnoreCase("MIXED") ?
                        guessQuestionType(qr.getQuestionText()) : request.getTestType().toUpperCase()))
                .questionText(qr.getQuestionText())
                .correctAnswer(qr.getCorrectAnswer())
                .explanation(qr.getExplanation())
                .language(request.getLanguage())
                .difficulty(Test.Difficulty.valueOf(request.getDifficulty().toUpperCase()))
                .points(10)
                .build();

            // Serialize MCQ options
            if (qr.getOptions() != null && !qr.getOptions().isEmpty()) {
                try {
                    question.setOptions(objectMapper.writeValueAsString(qr.getOptions()));
                } catch (Exception e) {
                    log.error("Failed to serialize options: {}", e.getMessage());
                }
            }

            questions.add(question);
        }

        questionRepository.saveAll(questions);
        test.setTotalQuestions(questions.size());
        test = testRepository.save(test);

        return buildTestResponse(test, questions, false);
    }

    // ---------------------------------------------------------------
    // Submit Answers & Evaluate
    // ---------------------------------------------------------------
    @Transactional
    public TestResponse submitAnswers(AnswerSubmitRequest request, User user) {
        Test test = testRepository.findById(request.getTestId())
            .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        if (!test.getUser().getId().equals(user.getId())) {
            throw new SecurityException("Unauthorized access to test");
        }

        List<Question> questions = questionRepository.findByTestIdOrderByQuestionNumber(test.getId());
        List<Answer> answers = new ArrayList<>();
        List<String> wrongQuestions = new ArrayList<>();
        int correct = 0;
        double totalPoints = 0;

        for (Question question : questions) {
            String userAnswer = request.getAnswers().get(question.getId());
            if (userAnswer == null || userAnswer.isBlank()) continue;

            boolean isCorrect = evaluateAnswer(question, userAnswer);
            double points = isCorrect ? question.getPoints() : 0;

            Answer answer = Answer.builder()
                .question(question)
                .user(user)
                .test(test)
                .userAnswer(userAnswer)
                .isCorrect(isCorrect)
                .pointsEarned(points)
                .build();

            answers.add(answer);

            if (isCorrect) {
                correct++;
                totalPoints += points;
            } else {
                wrongQuestions.add(question.getQuestionText().substring(0, Math.min(100, question.getQuestionText().length())));
            }
        }

        answerRepository.saveAll(answers);

        // Calculate score
        double score = questions.isEmpty() ? 0 : (double) correct / questions.size() * 100;

        // Update test
        test.setStatus(Test.Status.COMPLETED);
        test.setAttemptedQuestions(answers.size());
        test.setCorrectAnswers(correct);
        test.setScore(score);
        test.setTimeTaken(request.getTimeTaken() != null ? request.getTimeTaken() : 0);
        test.setCompletedAt(LocalDateTime.now());
        testRepository.save(test);

        // Generate AI feedback
        String feedbackJson = aiService.generateTestFeedback(
            test.getLanguage(), test.getTopic().getName(), score,
            questions.size(), correct, test.getDifficulty().name(), wrongQuestions
        );
        AiFeedback feedback = parseAndSaveFeedback(feedbackJson, test, user);

        // Update progress
        updateProgress(user, test.getTopic(), test.getLanguage(), score, test.getDifficulty());

        // Update user stats
        user.setTotalTests(user.getTotalTests() + 1);
        user.setTotalScore(user.getTotalScore() + score);
        userRepository.save(user);

        return buildTestResponse(test, questions, true);
    }

    // ---------------------------------------------------------------
    // Get Test Result
    // ---------------------------------------------------------------
    public TestResponse getTestResult(Long testId, User user) {
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new ResourceNotFoundException("Test not found"));

        if (!test.getUser().getId().equals(user.getId()) &&
            !user.getRole().equals(User.Role.ADMIN)) {
            throw new SecurityException("Unauthorized");
        }

        List<Question> questions = questionRepository.findByTestIdOrderByQuestionNumber(testId);
        return buildTestResponse(test, questions, true);
    }

    // ---------------------------------------------------------------
    // Get User's Recent Tests
    // ---------------------------------------------------------------
    public List<TestResponse> getUserTests(User user) {
        return testRepository.findByUserIdOrderByStartedAtDesc(user.getId())
            .stream()
            .map(t -> buildTestResponse(t, null, false))
            .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------
    private boolean evaluateAnswer(Question question, String userAnswer) {
        if (question.getCorrectAnswer() == null) return false;

        // For MCQ - simple comparison
        if (question.getQuestionType() == Question.QuestionType.MCQ ||
            question.getQuestionType() == Question.QuestionType.OUTPUT) {
            return userAnswer.trim().equalsIgnoreCase(question.getCorrectAnswer().trim()) ||
                   userAnswer.trim().toUpperCase().startsWith(question.getCorrectAnswer().trim().toUpperCase());
        }

        // For theory/coding - check key concepts are present (simplified)
        String correctLower = question.getCorrectAnswer().toLowerCase();
        String userLower = userAnswer.toLowerCase();

        // Extract key words from correct answer
        String[] keyWords = correctLower.split("\\s+");
        int matchedWords = 0;
        int threshold = Math.max(3, keyWords.length / 5);

        for (String word : keyWords) {
            if (word.length() > 4 && userLower.contains(word)) {
                matchedWords++;
            }
        }

        return matchedWords >= threshold;
    }

    private String guessQuestionType(String questionText) {
        if (questionText.contains("output") || questionText.contains("Output")) return "OUTPUT";
        if (questionText.contains("```") || questionText.contains("code") || questionText.contains("implement")) return "CODING";
        return "THEORY";
    }

    private AiFeedback parseAndSaveFeedback(String feedbackJson, Test test, User user) {
        try {
            String cleaned = feedbackJson.trim()
                .replaceAll("```json\\n?", "")
                .replaceAll("```\\n?", "")
                .trim();

            JsonNode node = objectMapper.readTree(cleaned);

            AiFeedback feedback = AiFeedback.builder()
                .test(test)
                .user(user)
                .overallScore(test.getScore())
                .grade(node.path("grade").asText("N/A"))
                .strengths(node.path("strengths").asText(""))
                .weaknesses(node.path("weaknesses").asText(""))
                .mistakes(node.path("mistakes").asText(""))
                .improvementTips(node.path("improvementTips").asText(""))
                .studyRecommendations(node.path("studyRecommendations").asText(""))
                .detailedFeedback(node.path("overallFeedback").asText(""))
                .nextTopics(node.path("nextTopics").toString())
                .build();

            return aiFeedbackRepository.save(feedback);
        } catch (Exception e) {
            log.error("Failed to parse feedback: {}", e.getMessage());
            return aiFeedbackRepository.save(AiFeedback.builder()
                .test(test).user(user).overallScore(test.getScore()).grade("N/A")
                .detailedFeedback("AI feedback generation failed. Please review your answers manually.").build());
        }
    }

    private void updateProgress(User user, Topic topic, String language, double score, Test.Difficulty difficulty) {
        Progress progress = progressRepository
            .findByUserIdAndTopicIdAndLanguage(user.getId(), topic.getId(), language)
            .orElse(Progress.builder().user(user).topic(topic).language(language).build());

        progress.setTotalTests(progress.getTotalTests() + 1);
        progress.setLastTested(java.time.LocalDate.now());

        // Update avg score
        double newAvg = (progress.getAvgScore() * (progress.getTotalTests() - 1) + score) / progress.getTotalTests();
        progress.setAvgScore(newAvg);
        progress.setBestScore(Math.max(progress.getBestScore(), score));

        switch (difficulty) {
            case EASY -> progress.setEasyCompleted(progress.getEasyCompleted() + 1);
            case MEDIUM -> progress.setMediumCompleted(progress.getMediumCompleted() + 1);
            case HARD -> progress.setHardCompleted(progress.getHardCompleted() + 1);
        }

        progress.updateMasteryLevel();
        progressRepository.save(progress);
    }

    private TestResponse buildTestResponse(Test test, List<Question> questions, boolean includeAnswers) {
        TestResponse response = TestResponse.builder()
            .id(test.getId())
            .topicName(test.getTopic().getName())
            .language(test.getLanguage())
            .difficulty(test.getDifficulty().name())
            .testType(test.getTestType().name())
            .status(test.getStatus().name())
            .totalQuestions(test.getTotalQuestions())
            .attemptedQuestions(test.getAttemptedQuestions())
            .correctAnswers(test.getCorrectAnswers())
            .score(test.getScore())
            .timeTaken(test.getTimeTaken())
            .startedAt(test.getStartedAt())
            .completedAt(test.getCompletedAt())
            .build();

        if (questions != null) {
            List<QuestionResponse> qResponses = questions.stream().map(q -> {
                QuestionResponse qr = new QuestionResponse();
                qr.setId(q.getId());
                qr.setQuestionNumber(q.getQuestionNumber());
                qr.setQuestionType(q.getQuestionType().name());
                qr.setQuestionText(q.getQuestionText());
                qr.setLanguage(q.getLanguage());
                qr.setDifficulty(q.getDifficulty().name());
                qr.setPoints(q.getPoints());

                // Parse MCQ options
                if (q.getOptions() != null) {
                    try {
                        List<QuestionResponse.McqOption> options = objectMapper.readValue(
                            q.getOptions(), new TypeReference<>() {});
                        qr.setOptions(options);
                    } catch (Exception e) {
                        log.error("Failed to parse options for question {}", q.getId());
                    }
                }

                if (includeAnswers) {
                    qr.setCorrectAnswer(q.getCorrectAnswer());
                    qr.setExplanation(q.getExplanation());
                    if (q.getAnswer() != null) {
                        qr.setUserAnswer(q.getAnswer().getUserAnswer());
                        qr.setIsCorrect(q.getAnswer().getIsCorrect());
                    }
                }
                return qr;
            }).collect(Collectors.toList());

            response.setQuestions(qResponses);
        }

        // Add feedback if available
        if (includeAnswers) {
            aiFeedbackRepository.findByTestId(test.getId()).ifPresent(fb -> {
                FeedbackResponse feedbackResponse = buildFeedbackResponse(fb);
                response.setFeedback(feedbackResponse);
            });
        }

        return response;
    }

    private FeedbackResponse buildFeedbackResponse(AiFeedback fb) {
        List<String> nextTopics = new ArrayList<>();
        try {
            if (fb.getNextTopics() != null) {
                JsonNode arr = objectMapper.readTree(fb.getNextTopics());
                if (arr.isArray()) {
                    for (JsonNode n : arr) nextTopics.add(n.asText());
                }
            }
        } catch (Exception ignored) {}

        return FeedbackResponse.builder()
            .id(fb.getId())
            .overallScore(fb.getOverallScore())
            .grade(fb.getGrade())
            .strengths(fb.getStrengths())
            .weaknesses(fb.getWeaknesses())
            .mistakes(fb.getMistakes())
            .improvementTips(fb.getImprovementTips())
            .studyRecommendations(fb.getStudyRecommendations())
            .detailedFeedback(fb.getDetailedFeedback())
            .nextTopics(nextTopics)
            .build();
    }
}
