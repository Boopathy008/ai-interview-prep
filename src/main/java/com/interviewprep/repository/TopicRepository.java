package com.interviewprep.repository;

import com.interviewprep.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    Optional<Topic> findBySlug(String slug);
    List<Topic> findByIsActiveTrue();
    List<Topic> findByCategoryAndIsActiveTrue(Topic.Category category);
}
