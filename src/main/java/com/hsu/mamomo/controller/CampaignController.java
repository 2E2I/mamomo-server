package com.hsu.mamomo.controller;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CampaignController {
    private final CampaignService campaignService;

    @GetMapping("/campaigns")
    public List<Campaign> getAllCampaigns() {
        return campaignService.findAll();
    }
}
