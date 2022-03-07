package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.repository.CampaignRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CampaignService {

    private final CampaignRepository campaignRepository;

    public List<Campaign> findAll() {
        return campaignRepository.findAll();
    }
}
