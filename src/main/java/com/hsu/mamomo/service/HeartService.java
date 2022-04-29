package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.ALREADY_HEARTED;
import static com.hsu.mamomo.controller.exception.ErrorCode.CAMPAIGN_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.HEART_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.INVALID_JWT_TOKEN;
import static com.hsu.mamomo.controller.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.MISMATCH_JWT_USER;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.HeartDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;
    private User user;

    public void heart(HeartDto heartDto, String jwtToken) throws IOException {
        validateToken(heartDto, jwtToken);

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

    public void unHeart(HeartDto heartDto, String jwtToken) throws IOException {
        validateToken(heartDto, jwtToken);

        Optional<Heart> heartOpt = findHeartWithUserAndCampaignId(heartDto);

        if (heartOpt.isEmpty()) {
            throw new CustomException(HEART_NOT_FOUND);
        }

        heartRepository.delete(heartOpt.get());

        updateHeartCount(heartDto.getCampaignId(), -1);
    }

    public void validateToken(HeartDto heartDto, String jwtToken) {
        // 유효한 토큰인지 검증
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new CustomException(INVALID_JWT_TOKEN);
        }

        // 해당 유저 존재하는지 검증
        Optional<User> userOpt = userRepository.findUserById(heartDto.getUserId());
        if (userOpt.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        } else {
            user = userOpt.get();
        }

        // 토큰 정보와 요청 userId 정보가 같은지 검증
        if (!jwtTokenProvider.getUserPk(jwtToken).equals(userOpt.get().getEmail())) {
            throw new CustomException(MISMATCH_JWT_USER);
        }
    }

    public Optional<Heart> findHeartWithUserAndCampaignId(HeartDto heartDto) {
        return heartRepository
                .findHeartByUserAndCampaignId(user, heartDto.getCampaignId());
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
