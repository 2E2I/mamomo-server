package com.hsu.mamomo.service.factory;

import com.hsu.mamomo.domain.Campaign;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public abstract class ElasticSortFactory {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final String CAMPAIGN_INDEX = "campaigns";
    private static final Map<String, SortOrder> sortMap = createSortMap();

    private static Map<String, SortOrder> createSortMap() {
        return Map.of("asc", SortOrder.ASC, "desc", SortOrder.DESC);
    }

    public static FieldSortBuilder createSortBuilder(String item, String direction) {
        return SortBuilders.fieldSort(item).order(sortMap.get(direction));
    }

    public static Query createBasicQuery(String item, String direction) {
        return new NativeSearchQueryBuilder()
                .withSorts(createSortBuilder(item, direction))
                .build();
    }

    public abstract NativeSearchQuery createQuery(String keyword, String item, String direction);

    public List<Campaign> getCampaignList(Query query) {
        SearchHits<Campaign> searchHits =
                elasticsearchOperations.search(
                        query,
                        Campaign.class,
                        IndexCoordinates.of(CAMPAIGN_INDEX));

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

    }

}
