package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.ALREADY_HEARTED;
import static com.hsu.mamomo.controller.exception.ErrorCode.HEART_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.controller.exception.ErrorCode;
import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.HeartDto;
import com.hsu.mamomo.repository.jpa.HeartRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import java.util.Optional;
import javax.swing.text.html.Option;
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

        // 이미 좋아요 된 캠페인일 경우 409 에러
        if (findHeartWithUserAndCampaignId(heartDto).isPresent())
            throw new CustomException(ALREADY_HEARTED);

        Heart heart = Heart.builder()
                .campaignId(heartDto.getCampaignId())
                .user(userRepository.findUserById(heartDto.getUserId()).get())
                .build();
        heartRepository.save(heart);
    }

    public void unHeart(HeartDto heartDto) {
        Optional<Heart> heartOpt = findHeartWithUserAndCampaignId(heartDto);

        if (heartOpt.isEmpty())
            throw new CustomException(HEART_NOT_FOUND);

        heartRepository.delete(heartOpt.get());
    }

    public Optional<Heart> findHeartWithUserAndCampaignId(HeartDto heartDto) {
        Optional<User> userOpt = userRepository.findUserById(heartDto.getUserId());
        if (userOpt.isEmpty())
            throw new CustomException(MEMBER_NOT_FOUND);

        return heartRepository.findHeartByUserAndCampaignId(userOpt.get(), heartDto.getCampaignId());
    }

}
