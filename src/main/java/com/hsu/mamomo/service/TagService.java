package com.hsu.mamomo.service;

import static java.util.stream.Collectors.toList;

import com.hsu.mamomo.service.factory.ElasticTagFactory;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagService {

    private final RestHighLevelClient elasticsearchClient;
    private final ElasticTagFactory elasticTagFactory;

    /*
     * 상위 태그 반환
     * */
    @SneakyThrows
    public List<String> getRangeTags(int from, int to) {
        return elasticTagFactory.getTags(from, to);
    }
}
