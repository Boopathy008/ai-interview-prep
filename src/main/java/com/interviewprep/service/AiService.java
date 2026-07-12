package com.interviewprep.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interviewprep.dto.response.QuestionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            You are an expert technical interviewer and coding mentor for software developers.
            You specialize in creating interview questions, evaluating answers, and giving detailed feedback.
            Always respond with valid, well-structured JSON as requested — no markdown fences, no extra text.
            Be encouraging, constructive, and educational in your feedback.
            Tailor all content to the specified programming language and topic.
            """;

    // ---------------------------------------------------------------
    // Generate Questions via AI
    // ---------------------------------------------------------------
    public List<QuestionResponse> generateQuestions(String language, String topic,
                                                     String difficulty, String questionType,
                                                     int count) {
        String prompt = buildPrompt(language, topic, difficulty, questionType, count);
        String response = callGemini(prompt);
        return parseQuestions(response, questionType);
    }

    // ---------------------------------------------------------------
    // Generate comprehensive test feedback
    // ---------------------------------------------------------------
    public String generateTestFeedback(String language, String topic, double score,
                                        int totalQ, int correct, String difficulty,
                                        List<String> wrongQuestions) {
        String wrongs = wrongQuestions.isEmpty() ? "None"
                : String.join("\n- ", wrongQuestions.stream()
                        .limit(5).toList());

        String prompt = String.format("""
                Generate comprehensive interview test feedback.

                Language: %s | Topic: %s | Difficulty: %s
                Score: %.1f%% | Total Questions: %d | Correct: %d
                Questions answered incorrectly:
                - %s

                Respond ONLY with JSON (no markdown, no extra text):
                {
                  "grade": "A/B/C/D/F",
                  "overallFeedback": "Encouraging overall assessment",
                  "strengths": "What the student did well",
                  "weaknesses": "Areas needing improvement",
                  "mistakes": "Common mistakes observed",
                  "improvementTips": "3-5 actionable improvement tips",
                  "studyRecommendations": "Specific study advice",
                  "nextTopics": ["topic1", "topic2", "topic3"],
                  "readyForInterview": true,
                  "estimatedLevel": "Beginner/Intermediate/Advanced"
                }
                """, language, topic, difficulty, score, totalQ, correct, wrongs);

        return callGemini(prompt);
    }

    // ---------------------------------------------------------------
    // Evaluate a single answer
    // ---------------------------------------------------------------
    public EvaluationResult evaluateAnswer(String questionText, String correctAnswer,
                                            String userAnswer, String questionType,
                                            String language) {
        String prompt = String.format("""
                Evaluate this %s programming interview answer.

                Question: %s
                Expected Answer: %s
                Student Answer: %s
                Language: %s

                Respond ONLY with JSON:
                {
                  "isCorrect": true,
                  "score": 85,
                  "feedback": "Detailed feedback",
                  "mistakes": "Mistakes if any",
                  "betterApproach": "How to improve"
                }
                """, questionType, questionText, correctAnswer, userAnswer, language);

        String raw = callGemini(prompt);
        return parseEvaluation(raw);
    }

    // ---------------------------------------------------------------
    // Mock interview question
    // ---------------------------------------------------------------
    public String generateMockInterviewQuestion(String language, String topic,
                                                 String difficulty, int questionNumber,
                                                 String history) {
        String prompt = String.format("""
                You are a real technical interviewer at a top tech company.
                Conduct interview question #%d.

                Language: %s | Topic: %s | Difficulty: %s
                Previous context: %s

                Ask a natural, conversational question. Make it progressively harder.
                Respond ONLY with JSON:
                {
                  "question": "The interview question",
                  "questionType": "technical/coding/behavioral",
                  "hints": ["hint1"],
                  "expectedKeyPoints": ["key point 1", "key point 2"]
                }
                """, questionNumber, language, topic, difficulty,
                history.isEmpty() ? "This is the first question" : history);

        return callGemini(prompt);
    }

    // ---------------------------------------------------------------
    // Evaluate mock interview response
    // ---------------------------------------------------------------
    public String evaluateMockInterviewResponse(String question, String answer,
                                                 String language) {
        String prompt = String.format("""
                Evaluate this mock interview response as a senior interviewer.

                Question: %s
                Candidate Answer: %s
                Language: %s

                Respond ONLY with JSON:
                {
                  "score": 7,
                  "feedback": "Interviewer natural response and feedback",
                  "followUpQuestion": "Follow-up or null",
                  "strengths": "What was good",
                  "improvements": "What to improve",
                  "wouldHire": true,
                  "nextAction": "continue"
                }
                """, question, answer, language);

        return callGemini(prompt);
    }

    // ---------------------------------------------------------------
    // Generate study plan
    // ---------------------------------------------------------------
    public String generateStudyPlan(String language, String targetRole, int weeks,
                                     List<String> weakTopics, String experienceLevel) {
        String prompt = String.format("""
                Create a detailed %d-week study plan for a %s developer.

                Language: %s | Target Role: %s | Weak Topics: %s

                Respond ONLY with JSON:
                {
                  "title": "Study Plan Title",
                  "overview": "Brief overview",
                  "weeks": [
                    {
                      "weekNumber": 1,
                      "theme": "Week Theme",
                      "goals": ["goal1", "goal2"],
                      "days": [
                        {
                          "day": "Monday",
                          "topics": ["topic1"],
                          "exercises": ["exercise1"],
                          "resources": ["resource1"],
                          "estimatedHours": 2
                        }
                      ]
                    }
                  ],
                  "keyMilestones": ["milestone1"],
                  "interviewReadinessDate": "Week 4"
                }
                """, weeks, experienceLevel, language, targetRole,
                weakTopics.isEmpty() ? "General" : String.join(", ", weakTopics));

        return callGemini(prompt);
    }

    // ---------------------------------------------------------------
    // Analyse weak topics
    // ---------------------------------------------------------------
    public String analyzeWeakTopics(String language, List<String> topics,
                                     List<Double> scores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(topics.size(), scores.size()); i++) {
            sb.append(topics.get(i)).append(": ").append(String.format("%.0f", scores.get(i))).append("%\n");
        }

        String prompt = String.format("""
                Analyse these %s interview performance scores and identify weak areas.

                Topic Scores:
                %s

                Respond ONLY with JSON:
                {
                  "weakTopics": [{"topic": "name", "score": 40, "priority": "high"}],
                  "strongTopics": ["topic1"],
                  "overallPattern": "What the data suggests",
                  "rootCause": "Likely reason for weakness",
                  "actionPlan": ["step1", "step2", "step3"],
                  "timeToImprove": "2-3 weeks"
                }
                """, language, sb);

        return callGemini(prompt);
    }

    // ---------------------------------------------------------------
    // AI chat / mentor
    // ---------------------------------------------------------------
    public String chat(String userMessage, String language, String context) {
        String prompt = String.format("""
                You are an expert coding mentor helping a fresher developer.
                Language context: %s
                Context: %s

                Student question: %s

                Provide a helpful, educational response. Include code examples where relevant.
                Keep explanations clear and beginner-friendly.
                """, language, context != null ? context : "General", userMessage);

        return callGemini(prompt, false);
    }

    // ---------------------------------------------------------------
    // Code review
    // ---------------------------------------------------------------
    public String reviewCode(String code, String language, String problem) {
        String prompt = String.format("""
                Review this %s code as a senior developer.

                Problem: %s

                Code:
                ```
                %s
                ```

                Respond ONLY with JSON:
                {
                  "overallRating": "8",
                  "timeComplexity": "O(n)",
                  "spaceComplexity": "O(1)",
                  "correctness": true,
                  "codeQuality": "Good",
                  "issues": [{"type": "style", "description": "issue", "fix": "suggestion"}],
                  "strengths": ["strength1"],
                  "improvements": ["improvement1"],
                  "optimizedApproach": "Better solution description",
                  "sampleOptimizedCode": "optimized code snippet"
                }
                """, language, problem, code);

        return callGemini(prompt);
    }

    // ---------------------------------------------------------------
    // Private: call Gemini via Spring AI
    // ---------------------------------------------------------------
    private String callGemini(String userPrompt) {
        return callGemini(userPrompt, true);
    }

    private String callGemini(String userPrompt, boolean requireJson) {
        String sysPrompt = requireJson ? SYSTEM_PROMPT : """
                You are an expert technical interviewer and coding mentor for software developers.
                Be encouraging, constructive, and educational in your feedback.
                Use markdown formatting for your responses.
                """;
        try {
            return chatClient.prompt()
                    .system(sysPrompt)
                    .user(userPrompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("AI API call failed: {}", e.getMessage(), e);
            throw new RuntimeException("AI service temporarily unavailable. Please try again.", e);
        }
    }

    // ---------------------------------------------------------------
    // Private: Build question generation prompt
    // ---------------------------------------------------------------
    private String buildPrompt(String language, String topic, String difficulty,
                                String questionType, int count) {
        return switch (questionType.toUpperCase()) {
            case "MCQ" -> String.format("""
                    Generate %d %s difficulty MCQ questions for %s programming — topic: %s.

                    Respond ONLY with a JSON array:
                    [
                      {
                        "questionNumber": 1,
                        "questionText": "Question here",
                        "options": [
                          {"label":"A","text":"Option A"},
                          {"label":"B","text":"Option B"},
                          {"label":"C","text":"Option C"},
                          {"label":"D","text":"Option D"}
                        ],
                        "correctAnswer": "A",
                        "explanation": "Why A is correct"
                      }
                    ]
                    """, count, difficulty, language, topic);

            case "CODING" -> String.format("""
                    Generate %d %s difficulty coding problems for %s — topic: %s.

                    Respond ONLY with a JSON array:
                    [
                      {
                        "questionNumber": 1,
                        "questionText": "Problem description with examples and constraints",
                        "correctAnswer": "Complete working %s solution",
                        "explanation": "Algorithm approach, time complexity O(?), space O(?)"
                      }
                    ]
                    """, count, difficulty, language, topic, language);

            case "THEORY" -> String.format("""
                    Generate %d %s difficulty theory interview questions for %s — topic: %s.

                    Respond ONLY with a JSON array:
                    [
                      {
                        "questionNumber": 1,
                        "questionText": "Interview question",
                        "correctAnswer": "Comprehensive answer with key points",
                        "explanation": "Why this matters in interviews"
                      }
                    ]
                    """, count, difficulty, language, topic);

            case "OUTPUT" -> String.format("""
                    Generate %d %s difficulty "predict the output" MCQ questions for %s — topic: %s.

                    Respond ONLY with a JSON array:
                    [
                      {
                        "questionNumber": 1,
                        "questionText": "What is the output of this %s code?\\n\\n```%s\\n// code here\\n```",
                        "options": [
                          {"label":"A","text":"Output option A"},
                          {"label":"B","text":"Output option B"},
                          {"label":"C","text":"Output option C"},
                          {"label":"D","text":"Compile/Runtime Error"}
                        ],
                        "correctAnswer": "A",
                        "explanation": "Step-by-step execution trace"
                      }
                    ]
                    """, count, difficulty, language, topic, language, language);

            case "DEBUGGING" -> String.format("""
                    Generate %d %s difficulty debugging questions for %s — topic: %s.

                    Respond ONLY with a JSON array:
                    [
                      {
                        "questionNumber": 1,
                        "questionText": "Find and fix the bug in this %s code:\\n\\n```%s\\n// buggy code here\\n```",
                        "correctAnswer": "Fixed code and bug explanation",
                        "explanation": "What the bug was, why it happens, how to prevent it"
                      }
                    ]
                    """, count, difficulty, language, topic, language, language);

            default -> String.format("""
                    Generate %d %s %s interview questions for %s — topic: %s.
                    Mix of MCQ and theory. Respond as JSON array.
                    Each: {questionNumber, questionType, questionText, options (MCQ only), correctAnswer, explanation}
                    """, count, difficulty, questionType, language, topic);
        };
    }

    // ---------------------------------------------------------------
    // Private: Parse question JSON
    // ---------------------------------------------------------------
    private List<QuestionResponse> parseQuestions(String json, String questionType) {
        List<QuestionResponse> list = new ArrayList<>();
        try {
            String clean = cleanJson(json);
            JsonNode root = objectMapper.readTree(clean);
            if (!root.isArray()) return list;

            for (JsonNode node : root) {
                QuestionResponse q = new QuestionResponse();
                q.setQuestionNumber(node.path("questionNumber").asInt(1));
                q.setQuestionText(node.path("questionText").asText(""));
                q.setCorrectAnswer(node.path("correctAnswer").asText(""));
                q.setExplanation(node.path("explanation").asText(""));
                q.setQuestionType(questionType);
                q.setPoints(10);

                if (node.has("options") && node.get("options").isArray()) {
                    List<QuestionResponse.McqOption> options = new ArrayList<>();
                    for (JsonNode opt : node.get("options")) {
                        options.add(new QuestionResponse.McqOption(
                                opt.path("label").asText(),
                                opt.path("text").asText()));
                    }
                    q.setOptions(options);
                }
                list.add(q);
            }
        } catch (Exception e) {
            log.error("Failed to parse questions JSON: {}", e.getMessage());
            log.debug("Raw JSON: {}", json);
        }
        return list;
    }

    // ---------------------------------------------------------------
    // Private: Parse evaluation JSON
    // ---------------------------------------------------------------
    private EvaluationResult parseEvaluation(String json) {
        try {
            JsonNode node = objectMapper.readTree(cleanJson(json));
            return new EvaluationResult(
                    node.path("isCorrect").asBoolean(false),
                    node.path("score").asDouble(0),
                    node.path("feedback").asText(""),
                    node.path("mistakes").asText(""),
                    node.path("betterApproach").asText("")
            );
        } catch (Exception e) {
            log.error("Failed to parse evaluation: {}", e.getMessage());
            return new EvaluationResult(false, 0, "Evaluation unavailable", "", "");
        }
    }

    // ---------------------------------------------------------------
    // Private: Strip markdown fences from JSON
    // ---------------------------------------------------------------
    private String cleanJson(String raw) {
        if (raw == null) return "{}";
        return raw.trim()
                .replaceAll("(?s)```json\\s*", "")
                .replaceAll("(?s)```\\s*", "")
                .trim();
    }

    // ---------------------------------------------------------------
    // EvaluationResult value object
    // ---------------------------------------------------------------
    public static class EvaluationResult {
        private final boolean correct;
        private final double score;
        private final String feedback;
        private final String mistakes;
        private final String betterApproach;

        public EvaluationResult(boolean correct, double score, String feedback,
                                 String mistakes, String betterApproach) {
            this.correct = correct;
            this.score = score;
            this.feedback = feedback;
            this.mistakes = mistakes;
            this.betterApproach = betterApproach;
        }

        public boolean isCorrect() { return correct; }
        public double getScore() { return score; }
        public String getFeedback() { return feedback; }
        public String getMistakes() { return mistakes; }
        public String getBetterApproach() { return betterApproach; }
    }
}
