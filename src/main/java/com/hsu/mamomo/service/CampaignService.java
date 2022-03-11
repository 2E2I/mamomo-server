package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
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
    private final ElasticSortFactory factory;

    /*
     * 캠페인 전체 보기
     * */
    public List<Campaign> findAll(String item, String direction) {

        // 1. Setting up Builder
        FieldSortBuilder sortBuilder = factory.createSortBuilder(item, direction);

        // 2. Create Query
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withSorts(sortBuilder)
                .build();

        // 3. Execute search
        SearchHits<Campaign> searchHits = factory.getSearchHits(query);

        // 4. Map SearchHits to Campaign list
        return factory.getCampaignList(searchHits);
    }


}
