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
import java.util.Optional;
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

        if (!userId.isEmpty()) { // 로그인 된 상태일때 좋아요 정보까지 불러오기
            Optional<User> user = userRepository.findUserById(userId);
            if (user.isEmpty()) {
                throw new CustomException(MEMBER_NOT_FOUND);
            }

            List<Heart> hearts = user.get().getHearts();
            List<Campaign> campaigns = campaignDto.getCampaigns();

            for (Heart heart : hearts) {
                String campaignId = heart.getCampaignId();
                campaigns
                        .stream().filter(campaign -> campaign.getId().equals(campaignId))
                        .findFirst().get()
                        .setIsHeart(true);
            }
        }

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
