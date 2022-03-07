package com.hsu.mamomo.elastic;

import org.springframework.data.elasticsearch.core.DocumentOperations;
import org.springframework.data.elasticsearch.core.SearchOperations;

public interface ElasticsearchOperation extends DocumentOperations, SearchOperations {
}
