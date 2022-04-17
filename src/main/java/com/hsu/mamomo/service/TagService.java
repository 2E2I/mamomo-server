package com.hsu.mamomo.service;


import com.hsu.mamomo.service.factory.ElasticTagFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TagService {

    private final RestHighLevelClient elasticsearchClient;
    private final ElasticTagFactory elasticTagFactory;

    /*
     * 상위 태그 반환
     * */
    @SneakyThrows
    public List<String> getRangeTags(int from, int to) {
        return elasticTagFactory.getTags(from, to);
    }
}
