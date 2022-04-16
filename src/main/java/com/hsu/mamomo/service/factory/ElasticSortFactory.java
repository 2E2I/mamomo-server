package com.hsu.mamomo.service.factory;

import com.hsu.mamomo.domain.Campaign;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public abstract class ElasticSortFactory {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final String CAMPAIGN_INDEX = "campaigns";
    private static final Map<Direction, SortOrder> sortMap = createSortMap();

    private static Map<Direction, SortOrder> createSortMap() {
        return Map.of(Direction.ASC, SortOrder.ASC, Direction.DESC, SortOrder.DESC);
    }

    public static Order getOrder(Pageable pageable) {
        return pageable.getSort().stream().findFirst().get();
    }

    public static String getProperty(Pageable pageable) {
        return getOrder(pageable).getProperty();
    }

    public static Direction getDirection(Pageable pageable) {
        return getOrder(pageable).getDirection();
    }

    public static FieldSortBuilder createSortBuilder(Pageable pageable) {
        return SortBuilders
                .fieldSort(getProperty(pageable))
                .order(sortMap.get(getDirection(pageable)));
    }

    public static Query createBasicQuery(Pageable pageable) {
        return new NativeSearchQueryBuilder()
                .withSorts(createSortBuilder(pageable))
                .build();
    }

    public abstract NativeSearchQuery createQuery(String keyword, Pageable pageable);

    public List<Campaign> getCampaignSearchList(Query query) {
        SearchHits<Campaign> searchHits =
                elasticsearchOperations.search(
                        query,
                        Campaign.class,
                        IndexCoordinates.of(CAMPAIGN_INDEX));

        System.out.println("searchHits.getSearchHits().size() = " + searchHits.getSearchHits().size());

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());

    }

}
