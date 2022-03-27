package com.hsu.mamomo.repository.jpa;

import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartRepository extends JpaRepository<Heart, Long> {

    Optional<Heart> findHeartByUserAndCampaignId(User user, String campaignId);

}
