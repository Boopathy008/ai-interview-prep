package com.interviewprep.repository;

import com.interviewprep.entity.AiFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AiFeedbackRepository extends JpaRepository<AiFeedback, Long> {
    Optional<AiFeedback> findByTestId(Long testId);
    List<AiFeedback> findByUserIdOrderByCreatedAtDesc(Long userId);
}
