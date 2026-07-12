package com.interviewprep.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mock_interviews")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MockInterview {

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
    private Test.Difficulty difficulty;

    @Column(name = "target_role", length = 100)
    private String targetRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    @Builder.Default
    private CompanyType companyType = CompanyType.PRODUCT;

    @Column(name = "interview_transcript", columnDefinition = "LONGTEXT")
    private String interviewTranscript;

    @Column(name = "overall_score")
    private Double overallScore;

    @Column(name = "communication_score")
    private Double communicationScore;

    @Column(name = "technical_score")
    private Double technicalScore;

    @Column(name = "problem_solving_score")
    private Double problemSolvingScore;

    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Status status = Status.IN_PROGRESS;

    @Column(columnDefinition = "LONGTEXT")
    private String feedback;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
    }

    public enum CompanyType { STARTUP, MNC, PRODUCT, SERVICE }
    public enum Status { SCHEDULED, IN_PROGRESS, COMPLETED }
}
