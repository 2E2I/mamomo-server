package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.repository.elastic.CampaignRepository;
import com.hsu.mamomo.repository.jpa.HeartRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import com.hsu.mamomo.service.factory.ElasticCategoryFactory;
import com.hsu.mamomo.service.factory.ElasticSortFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

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

        // 로그인 된 상태일때 좋아요 정보까지 불러오기
        for (Campaign campaign : campaignDto.getCampaigns()) {
            Boolean isHearted = getIsHearted(campaign.getId(), userId);
            if (isHearted) {
                campaign.setIsHeart(true);
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

    public Boolean getIsHearted(String campaignId, String userId) {
        User user = userRepository.findUserById(userId).get();
        Optional<Heart> heart = heartRepository.findHeartByUserAndCampaignId(user, campaignId);
        return heart.isPresent();
    }

}
