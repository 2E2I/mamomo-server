package com.hsu.mamomo.service;

import static java.util.stream.Collectors.toList;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.repository.elastic.CampaignSearchRepository;
import com.hsu.mamomo.service.factory.ElasticSearchFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CampaignSearchService {

    private final CampaignSearchRepository campaignSearchRepository;
    private final ElasticSearchFactory factory;
    private final RestHighLevelClient elasticsearchClient;

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
        System.out.println("searchHits = " + searchHits);
        return factory.getCampaignList(searchHits);
    }


    /*
     * 상위 태그 반환
     * */
    @SneakyThrows
    public List<String> findTop10Tags() {

        TermsAggregationBuilder aggregation =
                AggregationBuilders.terms("tags")
                        .field("tags.keyword");

        SearchSourceBuilder builder = new SearchSourceBuilder().aggregation(aggregation);
        SearchRequest searchRequest = new SearchRequest("campaigns").source(builder);

        SearchResponse response = elasticsearchClient.search(searchRequest,
                RequestOptions.DEFAULT);

        Map<String, Aggregation> results = response.getAggregations()
                .asMap();

        ParsedStringTerms topTags = (ParsedStringTerms) results.get("tags");

        return topTags.getBuckets()
                .stream()
                .map(MultiBucketsAggregation.Bucket::getKeyAsString)
                .sorted()
                .collect(toList());
    }
}
