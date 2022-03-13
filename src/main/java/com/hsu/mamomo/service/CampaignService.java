package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
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

    private final CampaignRepository campaignRepository;
    private final ElasticSortFactory sortFactory;
    private final ElasticCategoryFactory categoryFactory;

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
