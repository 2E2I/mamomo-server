package com.hsu.mamomo.service;

import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class SortFactory {

    public Query createSortQuery(String type){
        Query sortQuery = null;
        switch (type){
            case "StartDate":
                sortQuery = new NativeSearchQueryBuilder()
                        .withSorts(SortBuilders.fieldSort("start_date").order(SortOrder.DESC))
                        .withSorts(SortBuilders.fieldSort("due_date").order(SortOrder.DESC))
                        .build();

                break;

            case "DueDate":
                sortQuery = new NativeSearchQueryBuilder()
                        .withSorts(SortBuilders.fieldSort("due_date").order(SortOrder.ASC))
                        .withSorts(SortBuilders.fieldSort("start_date").order(SortOrder.ASC))
                        .build();
                break;

        }

        return sortQuery;
    }

}
