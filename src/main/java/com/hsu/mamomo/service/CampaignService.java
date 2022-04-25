package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.CAMPAIGN_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.FAIL_ENCODING;
import static com.hsu.mamomo.controller.exception.ErrorCode.INVALID_JWT_TOKEN;
import static com.hsu.mamomo.controller.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.dto.CampaignInfoDto;
import com.hsu.mamomo.dto.CampaignInfoDto.CampaignInfoDtoBuilder;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.repository.elastic.CampaignRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import com.hsu.mamomo.service.factory.ElasticCategoryFactory;
import com.hsu.mamomo.service.factory.ElasticSearchFactory;
import com.hsu.mamomo.service.factory.ElasticSortFactory;
import com.hsu.mamomo.service.factory.ElasticTagFactory;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CampaignService {

    private final UserRepository userRepository;
    private final CampaignRepository campaignRepository;
    private final ElasticCategoryFactory categoryFactory;
    private final ElasticSearchFactory searchFactory;
    private final ElasticTagFactory tagFactory;

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

    public CampaignDto getCampaigns(
            Pageable pageable, Integer category_id, String keyword,
            String authorization, String tagName) {

        // 검색
        if (keyword != null) {
            campaignDto = new CampaignDto(
                    searchByTitleOrBody(keyword, pageable));
        } else {
            if (category_id != null) { // 카테고리 별 조회
                campaignDto = new CampaignDto(
                        findAllOfCategory(category_id, pageable));
            } else if (tagName != null) { // tag/{tagName}
                campaignDto = new CampaignDto(findAllOfTag(tagName, pageable));
            } else { // 전체보기
                campaignDto = new CampaignDto(
                        findAll(pageable));
            }
        }

        // check isHeart
        if (authorization != null) {
            String userId = getUserIdFromAuth(authorization);
            addIsHeartInfo(userId);
        }

        return campaignDto;
    }

    public CampaignInfoDto findCampaignById(String id) {
        Campaign campaign = campaignRepository.findById(id).orElse(null);

        if (campaign == null) {
            throw new CustomException(CAMPAIGN_NOT_FOUND);
        }

        CampaignInfoDtoBuilder campaignInfoDtoBuilder = CampaignInfoDto.builder()
                .id(campaign.getId())
                .siteType(campaign.getSiteType())
                .url(campaign.getUrl())
                .title(campaign.getTitle())
                .category(campaign.getCategory())
                .tags(campaign.getTags())
                .body(campaign.getBody())
                .organizationName(campaign.getOrganizationName())
                .dueDate(campaign.getDueDate())
                .startDate(campaign.getStartDate())
                .targetPrice(campaign.getTargetPrice())
                .statusPrice(campaign.getStatusPrice())
                .percent(campaign.getPercent());

        try {
            campaignInfoDtoBuilder
                    .thumbnail(getBase64EncodedImage(campaign.getThumbnail()));
        } catch (IOException e) {
            throw new CustomException(FAIL_ENCODING);
        }

        return campaignInfoDtoBuilder.build();
    }

    /*
     * 로그인 했을 경우
     * 좋아요(isHearted) true/false 정보 불러옴
     * */
    public void addIsHeartInfo(String userId) {

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

    /*
     * 캠페인 전체 보기
     * */
    public Page<Campaign> findAll(Pageable pageable) {
        return categoryFactory
                .getCampaignSearchList(ElasticSortFactory.createBasicQuery(pageable));
    }

    /*
     * 캠페인 카테고리 별로 보기
     * */
    public Page<Campaign> findAllOfCategory(Integer category_id, Pageable pageable) {
        String keyword = categoryFactory.matchCategoryNameByCategoryId(category_id);
        return categoryFactory
                .getCampaignSearchList(categoryFactory.createQuery(keyword, pageable));
    }

    /*
     * 캠페인 태그별로 보기
     * */
    public Page<Campaign> findAllOfTag(String tagName, Pageable pageable) {
        return tagFactory.getCampaignSearchList(tagFactory.createQuery(tagName, pageable));
    }

    /*
     * 캠페인 검색 결과 보기
     * 제목 + 본문 검색 (OR)
     * */
    public Page<Campaign> searchByTitleOrBody(String keyword, Pageable pageable) {
        return searchFactory.getCampaignSearchList(searchFactory.createQuery(keyword, pageable));
    }

    // thumbnail Image base64 encoding
    public static String getBase64EncodedImage(String imageURL) throws IOException {
        return Base64.encodeBase64String(IOUtils.toByteArray(new URL(imageURL).openStream()));
    }
}
