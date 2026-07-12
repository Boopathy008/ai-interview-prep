package com.interviewprep.repository;

import com.interviewprep.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByTestIdOrderByQuestionNumber(Long testId);
    long countByTestId(Long testId);
}
