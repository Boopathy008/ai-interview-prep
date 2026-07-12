package com.interviewprep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "question_number", nullable = false)
    private Integer questionNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "question_text", columnDefinition = "LONGTEXT", nullable = false)
    private String questionText;

    @Column(columnDefinition = "JSON")
    private String options; // JSON array for MCQ options

    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(length = 50)
    private String language;

    @Enumerated(EnumType.STRING)
    private Test.Difficulty difficulty;

    @Column
    @Builder.Default
    private Integer points = 10;

    @Column(name = "ai_generated")
    @Builder.Default
    private Boolean aiGenerated = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "question", cascade = CascadeType.ALL)
    private Answer answer;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum QuestionType {
        MCQ, CODING, THEORY, OUTPUT, DEBUGGING, FILL_BLANK
    }
}
