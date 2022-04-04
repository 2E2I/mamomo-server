package com.hsu.mamomo.controller;

import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.service.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/campaigns")
@RestController
public class CampaignController {

    private final CampaignService campaignService;


    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping
    public CampaignDto getAllCampaigns(
            @RequestParam(value = "sort", defaultValue = "none,none", required = false) String sort,
            @RequestParam(value = "category", required = false) Integer category_id,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        return campaignService.getCampaigns(sort, category_id, keyword, authorization);
    }

}
