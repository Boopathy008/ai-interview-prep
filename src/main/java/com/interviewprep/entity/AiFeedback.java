package com.interviewprep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_feedback")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AiFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(length = 5)
    private String grade;

    @Column(columnDefinition = "LONGTEXT")
    private String strengths;

    @Column(columnDefinition = "LONGTEXT")
    private String weaknesses;

    @Column(columnDefinition = "LONGTEXT")
    private String mistakes;

    @Column(name = "improvement_tips", columnDefinition = "LONGTEXT")
    private String improvementTips;

    @Column(name = "study_recommendations", columnDefinition = "LONGTEXT")
    private String studyRecommendations;

    @Column(name = "detailed_feedback", columnDefinition = "LONGTEXT")
    private String detailedFeedback;

    @Column(name = "next_topics", columnDefinition = "JSON")
    private String nextTopics;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
