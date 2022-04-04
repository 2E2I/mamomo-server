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

    public CampaignDto getCampaigns(String sort, Integer category_id, String keyword,
            String authorization) {
        String[] _sort = sort.split(","); // sort = [field, direction]

        if (keyword != null) {
            campaignDto = new CampaignDto(
                    searchByTitleOrBody(keyword, _sort[0], _sort[1]));
        } else {
            if (Objects.equals(_sort[0], "none") && Objects.equals(_sort[1], "none")) {
                _sort[0] = "start_date";
                _sort[1] = "desc";
            }

            if (category_id != null) {
                campaignDto = new CampaignDto(
                        findAllOfCategory(category_id, _sort[0], _sort[1]));
            } else {
                campaignDto = new CampaignDto(
                        findAll(_sort[0], _sort[1]));
            }
        }

        if(authorization!=null){
            String userId = getUserIdFromAuth(authorization);
            campaignDto = addHeartInfo(userId);
        }

        return campaignDto;

    }

    /*
     * 로그인 했을 경우
     * 좋아요(isHearted) true/false 정보 불러옴
     * */
    public CampaignDto addHeartInfo(String userId) {
        if (!userId.equals("")) {
            Optional<User> user = userRepository.findUserById(userId);
            log.info("userID = {}", userId);
            log.info("로그인 된 유저 = {}", user);
            if (user.isEmpty()) {
                throw new CustomException(MEMBER_NOT_FOUND);
            }

            List<Heart> hearts = user.get().getHearts();
            List<Campaign> campaigns = campaignDto.getCampaigns();

            for (Heart heart : hearts) {
                String campaignId = heart.getCampaignId();
                Optional<Campaign> campaignOpt = campaigns
                        .stream().filter(campaign -> Objects.equals(campaign.getId(), campaignId))
                        .findFirst();
                campaignOpt.ifPresent(campaign -> campaign.setIsHeart(true));
            }
        }

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
    public List<Campaign> findAll(String item, String direction) {
        return categoryFactory.getCampaignList(ElasticSortFactory.createBasicQuery(item, direction));
    }

    /*
     * 캠페인 카테고리 별로 보기
     * */
    public List<Campaign> findAllOfCategory(Integer category_id, String item, String direction) {
        String keyword = categoryFactory.matchCategoryNameByCategoryId(category_id);
        return categoryFactory.getCampaignList(categoryFactory.createQuery(keyword, item, direction));
    }

    /*
     * 캠페인 검색 결과 보기
     * 제목 + 본문 검색 (OR)
     * */
    public List<Campaign> searchByTitleOrBody(String keyword, String item, String direction) {
        return searchFactory.getCampaignList(searchFactory.createQuery(keyword, item, direction));
    }
}
