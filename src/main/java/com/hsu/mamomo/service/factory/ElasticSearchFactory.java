package com.hsu.mamomo.service.factory;

import com.hsu.mamomo.domain.Campaign;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ElasticSearchFactory extends ElasticFactory {

    public ElasticSearchFactory(ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    public MultiMatchQueryBuilder createQueryBuilder(String keyword) {
        return QueryBuilders.multiMatchQuery(keyword, "title", "body")
                .operator(Operator.AND);
    }


    @Override
    public List<Campaign> getCampaignList(SearchHits<Campaign> searchHits) {
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
