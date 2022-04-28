package com.hsu.mamomo.service.factory;

import java.util.List;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticHeartFactory extends ElasticSortFactory {

    public ElasticHeartFactory(
            ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    @Override
    public NativeSearchQuery createQuery(Object campaignIdListByHeart, Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.termsQuery("campaign_id", ((List<String>) campaignIdListByHeart)))
                .withPageable(pageable);

        return queryBuilder.build();
    }
}
