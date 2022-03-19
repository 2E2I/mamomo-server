package com.hsu.mamomo.repository;

import com.hsu.mamomo.domain.Campaign;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignSearchRepository extends ElasticsearchRepository<Campaign, String> {


}
