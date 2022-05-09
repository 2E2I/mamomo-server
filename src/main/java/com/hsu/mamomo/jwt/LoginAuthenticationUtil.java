package com.hsu.mamomo.jwt;

import static com.hsu.mamomo.controller.exception.ErrorCode.INVALID_JWT_TOKEN;
import static com.hsu.mamomo.controller.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.MISMATCH_JWT_USER;
import static com.hsu.mamomo.controller.exception.ErrorCode.UNAUTHORIZED;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.dto.banner.BannerSaveDto;
import com.hsu.mamomo.repository.jpa.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoginAuthenticationUtil {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /*
     * JWT 토큰에서 유저 아이디 추출
     * */
    public String getUserIdByJwtToken(String token) {
        Optional<User> user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token));
        if (user.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }
        return user.get().getId();
    }

    /*
     * JWT 토큰에서 유저 이메일 추출
     * */
    public String getUserEmailByJwtToken(String token) {
        Optional<User> user = userRepository.findByEmail(jwtTokenProvider.getUserPk(token));
        if (user.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }
        return user.get().getEmail();
    }

    /*
     * JWT 토큰 유효 검증
     * */
    public String getUserIdFromAuth(String authorization) {

        String jwtToken = authorization.substring(7);

        // 유효한 토큰인지 검증
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new CustomException(INVALID_JWT_TOKEN);
        }

        return getUserIdByJwtToken(jwtToken);
    }

    public String getUserEmailFromAuth(String authorization) {

        String jwtToken = authorization.substring(7);

        // 유효한 토큰인지 검증
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new CustomException(INVALID_JWT_TOKEN);
        }

        return getUserEmailByJwtToken(jwtToken);
    }

    /*
     * 로그인 했을 경우
     * JWT 토큰 검증 후 캠페인 좋아요 정보 추가
     * */
    public void checkAuthAndAddHeartInfo(String authorization, CampaignDto campaignDto) {
        if (authorization != null) {
            String userId = getUserIdFromAuth(authorization);
            addIsHeartInfo(userId, campaignDto);
        }
    }

    /*
     * 좋아요(isHearted) true/false 정보를 CampaignDto에 추가
     * */
    public void addIsHeartInfo(String userId, CampaignDto campaignDto) {

        Optional<User> user = userRepository.findUserById(userId);

        if (user.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        List<Heart> hearts = user.get().getHearts();

        for (Heart heart : hearts) {
            String campaignId = heart.getCampaignId();
            Optional<Campaign> campaignOpt = campaignDto.getCampaigns().getContent()
                    .stream().filter(campaign -> Objects.equals(campaign.getId(), campaignId))
                    .findFirst();
            campaignOpt.ifPresent(campaign -> campaign.setIsHeart(true));
        }

    }

    public User getUserIdByEmail(String authorization, String email) {
        if (authorization == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        String userId = user.get().getId();

        if (!getUserIdFromAuth(authorization).equals(userId)) {
            throw new CustomException(MISMATCH_JWT_USER);
        }

        return user.get();
    }

}
