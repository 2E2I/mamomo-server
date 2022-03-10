package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CampaignService {
    private final CampaignRepository campaignRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final SortFactory sortFactory;
    private static final String CAMPAIGN_INDEX = "campaigns";


    /*
     * 캠페인 전체 보기 - 최신 순
     * */
    public List<Campaign> findAllByStartDate() {
        // 1. Create sort Query
        Query sortQuery = sortFactory.createSortQuery("StartDate");

        // 2. Execute search
        SearchHits<Campaign> campaignHits =
                elasticsearchOperations.search(sortQuery,
                        Campaign.class,
                        IndexCoordinates.of(CAMPAIGN_INDEX)
                );

        // 3. Map searchHits to Campaign list
        List<Campaign> campaignMatches = new ArrayList<Campaign>();
        campaignHits.forEach(
                searchHit -> {
                    campaignMatches.add(searchHit.getContent());
                });

        return campaignMatches;
    }


    /*
     * 캠페인 전체 보기 - 종료 임박 순
     * */
    public List<Campaign> findAllByDueDate() {
        // 1. Create sort Query
        Query sortQuery = sortFactory.createSortQuery("DueDate");

        // 2. Execute search
        SearchHits<Campaign> campaignHits =
                elasticsearchOperations.search(sortQuery,
                        Campaign.class,
                        IndexCoordinates.of(CAMPAIGN_INDEX)
                );

        // 3. Map searchHits to Campaign list
        List<Campaign> campaignMatches = new ArrayList<Campaign>();
        campaignHits.forEach(
                searchHit -> {
                    campaignMatches.add(searchHit.getContent());
                });

        return campaignMatches;
    }

}
