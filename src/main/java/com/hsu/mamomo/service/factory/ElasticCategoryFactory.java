package com.hsu.mamomo.service.factory;

import com.hsu.mamomo.domain.Campaign;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Component;

@Component
public class ElasticCategoryFactory extends ElasticFactory {

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

    public ElasticCategoryFactory(
            ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    public String matchCategoryNameByCategoryId(int category_id) {
        return categoryMap.get(category_id);
    }

    @Override
    public QueryBuilder createQueryBuilder(String keyword) {
        return QueryBuilders.matchQuery("category",keyword);
    }

    @Override
    public List<Campaign> getCampaignList(SearchHits<Campaign> searchHits) {
        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
