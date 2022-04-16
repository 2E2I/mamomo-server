package com.hsu.mamomo.repository.elastic;

import com.hsu.mamomo.domain.Campaign;
import java.util.Optional;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampaignRepository extends ElasticsearchRepository<Campaign, String> {

    List<Campaign> findAll();

    Optional<Campaign> findDistinctBySiteType(String siteType);

    @Override
    Optional<Campaign> findById(String s);
}
