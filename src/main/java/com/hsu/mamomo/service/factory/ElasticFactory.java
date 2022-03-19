package com.hsu.mamomo.service.factory;

import com.hsu.mamomo.domain.Campaign;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public abstract class ElasticFactory {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final String CAMPAIGN_INDEX = "campaigns";
    private static final Map<String, SortOrder> sortMap = createSortMap();

    private static Map<String, SortOrder> createSortMap() {
        return Map.of("asc", SortOrder.ASC, "desc", SortOrder.DESC);
    }

    public abstract QueryBuilder createQueryBuilder(String keyword);

    public FieldSortBuilder createSortBuilder(String item, String direction) {
        return SortBuilders.fieldSort(item).order(sortMap.get(direction));
    }

    public SearchHits<Campaign> getSearchHits(Query query) {
        return elasticsearchOperations.search(
                query,
                Campaign.class,
                IndexCoordinates.of(CAMPAIGN_INDEX)
        );
    }

    public abstract List<Campaign> getCampaignList(SearchHits<Campaign> searchHits);

}
