package com.hsu.mamomo.controller;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.dto.CampaignInfoDto;
import com.hsu.mamomo.service.CampaignService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CampaignController {

    private final CampaignService campaignService;


    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/campaigns")
    public CampaignDto getAllCampaigns(
            @PageableDefault(size = 20, sort = "start_date", direction = Direction.DESC) Pageable pageable,
            @RequestParam(value = "category", required = false) Integer category_id,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        return campaignService.getCampaigns(pageable, category_id, keyword, authorization, null);
    }

    @GetMapping("/campaign/{id}")
    public CampaignInfoDto getCampaignById(@PathVariable(value = "id") String id) {
        return campaignService.findCampaignById(id);
    }

}
