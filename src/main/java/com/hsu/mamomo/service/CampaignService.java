package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.MEMBER_NOT_FOUND;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.repository.jpa.HeartRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import com.hsu.mamomo.service.factory.ElasticCategoryFactory;
import com.hsu.mamomo.service.factory.ElasticSortFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.text.html.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CampaignService {

    private final UserRepository userRepository;
    private final ElasticSortFactory sortFactory;
    private final ElasticCategoryFactory categoryFactory;
    private final HeartRepository heartRepository;

    public CampaignDto getCampaigns(String sort, Integer category_id, String userId) {
        CampaignDto campaignDto;
        String[] _sort = sort.split(","); // sort = [field, direction]

        // 전체보기
        if (category_id != null) {
            campaignDto = new CampaignDto(
                    findAllOfCategory(_sort[0], _sort[1], category_id));
        } else { // 카테고리 별로 보기
            campaignDto = new CampaignDto(findAll(_sort[0], _sort[1]));
        }

        /*
         * 로그인 했을 경우
         * 좋아요(isHearted) true/false 정보 불러옴
         * */
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
                        .stream().filter(campaign -> campaign.getId().equals(campaignId))
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
                    .filter(v -> v.getId().equals(campaignId))
                    .findFirst();
            campaignOpt.ifPresent(campaign -> campaign.setHeartCount(count));
        });

        return campaignDto;
    }

    /*
     * 캠페인 전체 보기
     * */
    public List<Campaign> findAll(String item, String direction) {

        // 1. Setting up Builder
        FieldSortBuilder sortBuilder = sortFactory.createSortBuilder(item, direction);

        // 2. Create Query
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withSorts(sortBuilder)
                .build();

        // 3. Execute search
        SearchHits<Campaign> searchHits = sortFactory.getSearchHits(query);

        // 4. Map SearchHits to Campaign list
        return sortFactory.getCampaignList(searchHits);
    }

    /*
     * 캠페인 카테고리 별로 보기
     * */
    public List<Campaign> findAllOfCategory(String item, String direction, Integer category_id) {

        // 1. Setting up Builder
        String keyword = categoryFactory.matchCategoryNameByCategoryId(category_id);
        QueryBuilder queryBuilder = categoryFactory.createQueryBuilder(keyword);
        FieldSortBuilder sortBuilder = categoryFactory.createSortBuilder(item, direction);

        // 2. Create Query
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withSorts(sortBuilder)
                .build();

        // 3. Execute search
        SearchHits<Campaign> searchHits = sortFactory.getSearchHits(query);

        // 4. Map SearchHits to Campaign list
        return sortFactory.getCampaignList(searchHits);
    }

}
