package com.hsu.mamomo.service.factory;

import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;

import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchFactory extends ElasticSortFactory {

    public ElasticSearchFactory(ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    @Override
    public NativeSearchQuery createQuery(String keyword, String item, String direction) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "body")
                        .operator(Operator.AND));
        /*
         * none == 정확도순
         * none != 필드값 기준 정렬
         * */
        if (!item.equals("none")) {
            queryBuilder.withSorts(createSortBuilder(item, direction));
        }

        return queryBuilder.build();
    }
}
