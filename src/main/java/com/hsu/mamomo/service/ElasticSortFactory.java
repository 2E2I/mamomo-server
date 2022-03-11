package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import org.elasticsearch.index.query.QueryBuilder;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ElasticSortFactory extends ElasticFactory {

    public ElasticSortFactory(ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    @Override
    public QueryBuilder createQueryBuilder(String keyword) {
        return null;
    }


    @Override
    public List<Campaign> getCampaignList(SearchHits<Campaign> searchHits) {
        List<Campaign> campaignList = new ArrayList<Campaign>();
        searchHits.forEach(
                searchHit -> {
                    campaignList.add(searchHit.getContent());
                });
        return campaignList;
    }
}
