package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.ALREADY_HEARTED;
import static com.hsu.mamomo.controller.exception.ErrorCode.CAMPAIGN_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.HEART_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.HeartDto;
import com.hsu.mamomo.repository.elastic.CampaignRepository;
import com.hsu.mamomo.repository.jpa.HeartRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class HeartService {

    private final HeartRepository heartRepository;
    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final RestHighLevelClient elasticsearchClient;

    public void heart(HeartDto heartDto) throws IOException {

        // 이미 좋아요 된 캠페인일 경우 409 에러
        if (findHeartWithUserAndCampaignId(heartDto).isPresent()) {
            throw new CustomException(ALREADY_HEARTED);
        }

        Heart heart = Heart.builder()
                .campaignId(heartDto.getCampaignId())
                .user(userRepository.findUserById(heartDto.getUserId()).get())
                .build();
        heartRepository.save(heart);

        updateHeartCount(heartDto.getCampaignId(), 1);

    }

    public void unHeart(HeartDto heartDto) throws IOException {
        Optional<Heart> heartOpt = findHeartWithUserAndCampaignId(heartDto);

        if (heartOpt.isEmpty()) {
            throw new CustomException(HEART_NOT_FOUND);
        }

        heartRepository.delete(heartOpt.get());

        updateHeartCount(heartDto.getCampaignId(), -1);
    }

    public Optional<Heart> findHeartWithUserAndCampaignId(HeartDto heartDto) {
        Optional<User> userOpt = userRepository.findUserById(heartDto.getUserId());
        if (userOpt.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        return heartRepository
                .findHeartByUserAndCampaignId(userOpt.get(), heartDto.getCampaignId());
    }

    public void updateHeartCount(String campaignId, Integer plusOrMinus) throws IOException {

        Optional<Campaign> campaignOpt = campaignRepository.findById(campaignId);
        if (campaignOpt.isEmpty()) {
            throw new CustomException(CAMPAIGN_NOT_FOUND);
        }

        UpdateRequest request = new UpdateRequest("campaigns", campaignId)
                .doc("heart_count", campaignOpt.get().getHeartCount() + plusOrMinus);

        UpdateResponse response = elasticsearchClient.update(request, RequestOptions.DEFAULT);
        log.info("ES heartCount update response = {}", response);
    }

}
