package com.hsu.mamomo.service.factory;

import com.hsu.mamomo.dto.TextMiningResultDto;
import java.util.List;
import java.util.Map;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticTextMiningFactory extends ElasticSortFactory {

    private final Map<String, Float> fields = Map.of("body", 1.0f, "title", 2.0f); // field에 가중치 부여
    private final float MIN_SCORE = 30;

    public ElasticTextMiningFactory(
            ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    @Override
    public NativeSearchQuery createQuery(Object textMiningResultDto, Pageable pageable) {
        List<Map<String, String>> textMiningResults = ((TextMiningResultDto) textMiningResultDto).getResult();

        // 텍스트 키워드(keyword)에 가중치(value)를 부여하는 쿼리
        String query = "";
        for (Map<String, String> map : textMiningResults) {
            String keyword = map.get("keyword");
            String value = map.get("value");
            query += "(" + keyword + ")" + "^" + value + " OR ";
        }
        query = query.substring(0, query.length() - 4); // 맨 끝의 " OR " 제거
        System.out.println("query = " + query);

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery(query).fields(fields))
                .withMinScore(MIN_SCORE)
                .withPageable(pageable);

        return queryBuilder.build();
    }
}
