package com.hsu.mamomo.service.factory;

import java.util.Map;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticCategoryFactory extends ElasticSortFactory {

    public ElasticCategoryFactory(
            ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    private static final Map<Integer, String> categoryMap
            = Map.of(1, "아동|청소년",
            2, "어르신",
            3, "장애인",
            4, "어려운이웃",
            5, "다문화",
            6, "지구촌",
            7, "가족|여성",
            8, "우리사회",
            9, "동물",
            10, "환경");


    @Override
    public NativeSearchQuery createQuery(String keyword, Pageable pageable) {
        return new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("category", keyword))
                .withSorts(createSortBuilder(pageable))
                .build();
    }

    public String matchCategoryNameByCategoryId(int category_id) {
        return categoryMap.get(category_id);
    }

}
