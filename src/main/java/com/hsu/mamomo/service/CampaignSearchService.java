package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.repository.CampaignSearchRepository;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CampaignSearchService {
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    private final CampaignSearchRepository campaignSearchRepository;
    private static final Map<String, SortOrder> sortMap = createSortMap();
    private static Map<String, SortOrder> createSortMap() {
        return Map.of("asc", SortOrder.ASC, "desc", SortOrder.DESC);
    }

    /*
    * 제목 + 본문 검색 (OR)
    * */
    public List<Campaign> searchByTitleOrBody(String keyword, String item, String direction) {


        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword,"title", "body")
                .operator(Operator.AND);

        FieldSortBuilder sortBuilder = SortBuilders.fieldSort(item).order(sortMap.get(direction));

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQueryBuilder)
                .withSorts(sortBuilder)
                .build();

        SearchHits<Campaign> searchHit = elasticsearchOperations.search(searchQuery, Campaign.class, IndexCoordinates.of("campaigns"));
        System.out.println(searchHit);
        return searchHit.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
