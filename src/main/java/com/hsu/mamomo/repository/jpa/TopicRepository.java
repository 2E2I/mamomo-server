package com.hsu.mamomo.repository.jpa;

import com.hsu.mamomo.domain.Topic;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TopicRepository extends JpaRepository<Topic, Integer> {

    Optional<Topic> findTopicById(Integer id);
    List<Topic> findAll();
}
