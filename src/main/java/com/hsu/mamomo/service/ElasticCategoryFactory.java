package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import java.util.List;
import java.util.stream.Collectors;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

@Component
public class ElasticCategoryFactory extends ElasticFactory{

    public ElasticCategoryFactory(
            ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

        @Override
        public QueryBuilder createQueryBuilder(String keyword) {
            System.out.println("keyword = " + keyword);
            return QueryBuilders.matchQuery("category",keyword);
        }

        @Override
        public List<Campaign> getCampaignList(SearchHits<Campaign> searchHits) {
            return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
