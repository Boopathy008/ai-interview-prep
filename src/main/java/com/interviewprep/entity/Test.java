package com.interviewprep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false, length = 50)
    private String language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(name = "test_type", nullable = false)
    private TestType testType;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.IN_PROGRESS;

    @Column(name = "total_questions")
    @Builder.Default
    private Integer totalQuestions = 0;

    @Column(name = "attempted_questions")
    @Builder.Default
    private Integer attemptedQuestions = 0;

    @Column(name = "correct_answers")
    @Builder.Default
    private Integer correctAnswers = 0;

    @Column
    @Builder.Default
    private Double score = 0.0;

    @Column(name = "time_taken")
    @Builder.Default
    private Integer timeTaken = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @OneToOne(mappedBy = "test", cascade = CascadeType.ALL)
    private AiFeedback aiFeedback;

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }

    public enum Difficulty { EASY, MEDIUM, HARD }
    public enum TestType { MCQ, CODING, THEORY, MIXED, MOCK_INTERVIEW }
    public enum Status { IN_PROGRESS, COMPLETED, ABANDONED }
}
