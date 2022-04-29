package com.hsu.mamomo.service.factory;

import com.hsu.mamomo.dto.TextMiningResultDto;
import java.util.List;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticTextMiningFactory extends ElasticSortFactory {

    public ElasticTextMiningFactory(
            ElasticsearchOperations elasticsearchOperations) {
        super(elasticsearchOperations);
    }

    @Override
    public NativeSearchQuery createQuery(Object textMiningResultDto, Pageable pageable) {
        List<String> textMiningResults = ((TextMiningResultDto) textMiningResultDto).getResult();

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder()
                .withQuery(
                        QueryBuilders.boolQuery()
                                .must(QueryBuilders.termsQuery("body", textMiningResults.get(0)))
                                .should(QueryBuilders.termQuery("body", textMiningResults.get(1)))
                                .should(QueryBuilders.termQuery("body", textMiningResults.get(2)))
                )
                .withPageable(pageable);

        return queryBuilder.build();
    }
}
