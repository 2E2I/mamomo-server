package com.hsu.mamomo.service.factory;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticTagFactory extends ElasticSortFactory {

    private final RestHighLevelClient elasticsearchClient;

    public ElasticTagFactory(
            ElasticsearchOperations elasticsearchOperations,
            RestHighLevelClient elasticsearchClient) {
        super(elasticsearchOperations);
        this.elasticsearchClient = elasticsearchClient;
    }

    @Override
    public NativeSearchQuery createQuery(Object keyword, Pageable pageable) {
        return new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("tags.keyword", keyword))
                .withSorts(createSortBuilder(pageable))
                .withPageable(pageable)
                .build();
    }

    public List<String> getTags(int from, int to) throws IOException {
        TermsAggregationBuilder aggregation =
                AggregationBuilders.terms("tags")
                        .field("tags.keyword")
                        .size(to);

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
                .collect(toList())
                .subList(from, to);
    }
}
