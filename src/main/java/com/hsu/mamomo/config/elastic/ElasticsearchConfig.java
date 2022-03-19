package com.hsu.mamomo.config.elastic;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@EnableElasticsearchRepositories(basePackages = "com.hsu.mamomo.repository.elastic")
@Configuration
public class ElasticsearchConfig extends AbstractElasticsearchConfiguration {

    @Value("${elasticsearch.host}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Value("${elasticsearch.username}")
    private String username;

    @Value("${elasticsearch.password}")
    private String password;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {

        final ClientConfiguration clientConfiguration =
                ClientConfiguration
                        .builder()
                        .connectedTo(host + ":" + port)
                        .withBasicAuth(username, password)
                        .build();

        return RestClients.create(clientConfiguration).rest();
    }
}
