package com.interviewprep.repository;

import com.interviewprep.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUserId(Long userId);
    Optional<Progress> findByUserIdAndTopicIdAndLanguage(Long userId, Long topicId, String language);

    @Query("SELECT p FROM Progress p WHERE p.user.id = :userId ORDER BY p.avgScore ASC")
    List<Progress> findWeakTopicsByUserId(@Param("userId") Long userId);
}
