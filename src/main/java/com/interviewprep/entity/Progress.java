package com.interviewprep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "topic_id", "language"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Progress {

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

    @Column(name = "total_tests")
    @Builder.Default
    private Integer totalTests = 0;

    @Column(name = "avg_score")
    @Builder.Default
    private Double avgScore = 0.0;

    @Column(name = "best_score")
    @Builder.Default
    private Double bestScore = 0.0;

    @Column(name = "easy_completed")
    @Builder.Default
    private Integer easyCompleted = 0;

    @Column(name = "medium_completed")
    @Builder.Default
    private Integer mediumCompleted = 0;

    @Column(name = "hard_completed")
    @Builder.Default
    private Integer hardCompleted = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "mastery_level")
    @Builder.Default
    private MasteryLevel masteryLevel = MasteryLevel.BEGINNER;

    @Column(name = "last_tested")
    private LocalDate lastTested;

    public void updateMasteryLevel() {
        if (avgScore >= 90 && totalTests >= 5) masteryLevel = MasteryLevel.EXPERT;
        else if (avgScore >= 75 && totalTests >= 3) masteryLevel = MasteryLevel.ADVANCED;
        else if (avgScore >= 50 && totalTests >= 2) masteryLevel = MasteryLevel.INTERMEDIATE;
        else masteryLevel = MasteryLevel.BEGINNER;
    }

    public enum MasteryLevel { BEGINNER, INTERMEDIATE, ADVANCED, EXPERT }
}
