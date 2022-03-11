package com.hsu.mamomo.controller;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping("/campaigns")
    public List<Campaign> getAllCampaignsBySort(
            @RequestParam(value = "sort", defaultValue = "start_date,desc") String sort) {
        String[] _sort = sort.split(",");
        // sort = [field, direction]
        return campaignService.findAll(_sort[0], _sort[1]);
    }
}
