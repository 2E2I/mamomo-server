package com.hsu.mamomo.controller;

import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/api/campaigns")
@RestController
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping
    public CampaignDto getAllCampaigns(
            @RequestParam(value = "sort", defaultValue = "start_date,desc", required = false) String sort,
            @RequestParam(value = "category", required = false) Integer category_id) {

        CampaignDto campaignDto;
        String[] _sort = sort.split(",");
        // sort = [field, direction]

        if (category_id != null) {
            campaignDto = new CampaignDto(campaignService.findAllOfCategory(_sort[0], _sort[1], category_id));
        } else {
            campaignDto = new CampaignDto(campaignService.findAll(_sort[0], _sort[1]));
        }

        return campaignDto;
    }

}
