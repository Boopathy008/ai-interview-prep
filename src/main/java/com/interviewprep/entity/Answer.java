package com.interviewprep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @Column(name = "user_answer", columnDefinition = "LONGTEXT")
    private String userAnswer;

    @Column(name = "is_correct")
    @Builder.Default
    private Boolean isCorrect = false;

    @Column(name = "points_earned")
    @Builder.Default
    private Double pointsEarned = 0.0;

    @Column(name = "time_spent")
    @Builder.Default
    private Integer timeSpent = 0;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}
