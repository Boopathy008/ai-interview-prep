package com.interviewprep.repository;

import com.interviewprep.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<Test, Long> {
    List<Test> findByUserIdOrderByStartedAtDesc(Long userId);
    List<Test> findByUserIdAndStatusOrderByStartedAtDesc(Long userId, Test.Status status);

    @Query("SELECT t FROM Test t WHERE t.user.id = :userId AND t.topic.id = :topicId ORDER BY t.startedAt DESC")
    List<Test> findByUserAndTopic(@Param("userId") Long userId, @Param("topicId") Long topicId);

    @Query("SELECT COUNT(t) FROM Test t WHERE t.status = 'COMPLETED'")
    long countCompletedTests();

    @Query("SELECT AVG(t.score) FROM Test t WHERE t.user.id = :userId AND t.status = 'COMPLETED'")
    Double getAverageScoreByUserId(@Param("userId") Long userId);

    List<Test> findTop5ByUserIdAndStatusOrderByStartedAtDesc(Long userId, Test.Status status);
}
