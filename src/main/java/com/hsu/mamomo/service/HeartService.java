package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.HeartDto;
import com.hsu.mamomo.repository.jpa.HeartRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class HeartService {

    private final HeartRepository heartRepository;
    private final UserRepository userRepository;

    public void heart(HeartDto heartDto) {
        Heart heart = Heart.builder()
                .campaignId(heartDto.getCampaignId())
                .user(userRepository.findUserById(heartDto.getUserId()).get())
                .build();
        heartRepository.save(heart);
    }

    public void unHeart(HeartDto heartDto) {
        User user = userRepository.findUserById(heartDto.getUserId()).get();
        Heart heart = heartRepository.findHeartByUserAndCampaignId(user, heartDto.getCampaignId()).get();
        heartRepository.delete(heart);
    }

}
