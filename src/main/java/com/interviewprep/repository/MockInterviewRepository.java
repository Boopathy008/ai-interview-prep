package com.interviewprep.repository;

import com.interviewprep.entity.MockInterview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {
    List<MockInterview> findByUserIdOrderByStartedAtDesc(Long userId);
    List<MockInterview> findByUserIdAndStatus(Long userId, MockInterview.Status status);
}
