package com.hsu.mamomo.service;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagService {

    private final RestHighLevelClient elasticsearchClient;

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
