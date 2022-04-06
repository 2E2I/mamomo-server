package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.INVALID_JWT_TOKEN;
import static com.hsu.mamomo.controller.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.repository.jpa.HeartRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import com.hsu.mamomo.service.factory.ElasticCategoryFactory;
import com.hsu.mamomo.service.factory.ElasticSearchFactory;
import com.hsu.mamomo.service.factory.ElasticSortFactory;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CampaignService {

    private final UserRepository userRepository;
    private final ElasticCategoryFactory categoryFactory;
    private final ElasticSearchFactory searchFactory;
    private final HeartRepository heartRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    private CampaignDto campaignDto;

    public String getUserIdFromAuth(String authorization) {

        String jwtToken = authorization.substring(7);

        // 유효한 토큰인지 검증
        if (!jwtTokenProvider.validateToken(jwtToken)) {
            throw new CustomException(INVALID_JWT_TOKEN);
        }

        return userService.getUserIdByJwtToken(jwtToken);
    }

    public CampaignDto getCampaigns(Pageable pageable, Integer category_id, String keyword,
            String authorization) {

        // 검색
        if (keyword != null) {
            campaignDto = new CampaignDto(
                    searchByTitleOrBody(keyword, pageable));
        } else {
            if (category_id != null) { // 카테고리 별 조회
                campaignDto = new CampaignDto(
                        findAllOfCategory(category_id, pageable));
            } else { // 전체보기
                campaignDto = new CampaignDto(
                        findAll(pageable));
            }
        }

        if (authorization != null) {
            String userId = getUserIdFromAuth(authorization);
            campaignDto = addIsHeartInfo(userId);
        }

        // 좋아요 갯수 추가
        campaignDto = addHeartCountInfo();

        return campaignDto;
    }

    /*
     * 로그인 했을 경우
     * 좋아요(isHearted) true/false 정보 불러옴
     * */
    public CampaignDto addIsHeartInfo(String userId) {

        Optional<User> user = userRepository.findUserById(userId);

        if (user.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        List<Heart> hearts = user.get().getHearts();
        Page<Campaign> campaigns = campaignDto.getCampaigns();

        for (Heart heart : hearts) {
            String campaignId = heart.getCampaignId();
            Optional<Campaign> campaignOpt = campaigns
                    .stream().filter(campaign -> Objects.equals(campaign.getId(), campaignId))
                    .findFirst();
            campaignOpt.ifPresent(campaign -> campaign.setIsHeart(true));
        }

        return campaignDto;
    }

    public CampaignDto addHeartCountInfo() {

        /*
         * 캠페인당 좋아요 갯수
         * */
        List<Heart> hearts = heartRepository.findAll();
        Map<String, List<Heart>> heartMap = hearts.stream()
                .collect(Collectors.groupingBy(Heart::getCampaignId));
        heartMap.keySet().forEach(campaignId -> {
            int count = heartMap.get(campaignId).size(); // 해당 캠페인 좋아요 수
            Optional<Campaign> campaignOpt = campaignDto.getCampaigns().stream()
                    .filter(v -> Objects.equals(v.getId(), campaignId))
                    .findFirst();
            campaignOpt.ifPresent(campaign -> campaign.setHeartCount(count));
        });

        return campaignDto;
    }

    /*
     * 캠페인 전체 보기
     * */
    public Page<Campaign> findAll(Pageable pageable) {
        return categoryFactory
                .getCampaignSearchPage(ElasticSortFactory.createBasicQuery(pageable));
    }

    /*
     * 캠페인 카테고리 별로 보기
     * */
    public Page<Campaign> findAllOfCategory(Integer category_id, Pageable pageable) {
        String keyword = categoryFactory.matchCategoryNameByCategoryId(category_id);
        return categoryFactory
                .getCampaignSearchPage(categoryFactory.createQuery(keyword, pageable));
    }

    /*
     * 캠페인 검색 결과 보기
     * 제목 + 본문 검색 (OR)
     * */
    public Page<Campaign> searchByTitleOrBody(String keyword, Pageable pageable) {
        return searchFactory.getCampaignSearchPage(searchFactory.createQuery(keyword, pageable));
    }
}
