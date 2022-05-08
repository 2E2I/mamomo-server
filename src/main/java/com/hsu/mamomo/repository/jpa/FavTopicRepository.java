package com.hsu.mamomo.repository.jpa;

import com.hsu.mamomo.domain.FavTopic;
import com.hsu.mamomo.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavTopicRepository extends JpaRepository<FavTopic, Integer> {

    Optional<List<FavTopic>> findFavTopicByUser(User user);
}
