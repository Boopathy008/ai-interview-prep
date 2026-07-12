package com.interviewprep.repository;

import com.interviewprep.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByTestId(Long testId);
    Optional<Answer> findByQuestionId(Long questionId);

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.test.id = :testId AND a.isCorrect = true")
    long countCorrectByTestId(@Param("testId") Long testId);
}
