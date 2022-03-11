package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.repository.CampaignSearchRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CampaignSearchService {

    private final CampaignSearchRepository campaignSearchRepository;
    private final ElasticSearchFactory factory;

    /*
     * 제목 + 본문 검색 (OR)
     * */
    public List<Campaign> searchByTitleOrBody(String keyword, String item, String direction) {

        // 1. Setting up Builder
        MultiMatchQueryBuilder multiMatchQueryBuilder = factory.createQueryBuilder(keyword);

        // 2. Create Query
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQueryBuilder);

        /*
        * none == 정확도순
        * none != 필드값 기준 정렬
        * */
        if (!item.equals("none")) {
            FieldSortBuilder sortBuilder = factory.createSortBuilder(item, direction);
            queryBuilder.withSorts(sortBuilder);
        }

        NativeSearchQuery query = queryBuilder.build();

        // 3. Execute search
        SearchHits<Campaign> searchHits = factory.getSearchHits(query);

        // 4. Map SearchHits to Campaign list
        return factory.getCampaignList(searchHits);
    }
}
