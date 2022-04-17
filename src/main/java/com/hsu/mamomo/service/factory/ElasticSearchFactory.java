package com.hsu.mamomo.service.factory;

import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;

import org.springframework.data.domain.Pageable;
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
    public NativeSearchQuery createQuery(String keyword, Pageable pageable) {
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "body")
                        .operator(Operator.OR))
                .withPageable(pageable);
        /*
         * pageable -> sort -> order: property(정렬기준), direction(정렬방향)
         * property: none == 정확도순. 정렬 X
         * property: none != 필드값 기준 정렬
         * */

        if (!ElasticSortFactory.getProperty(pageable).equals("none")) {
            queryBuilder.withSorts(createSortBuilder(pageable));
        }

        return queryBuilder.build();
    }
}
