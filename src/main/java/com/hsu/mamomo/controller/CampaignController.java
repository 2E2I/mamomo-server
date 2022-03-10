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
    public List<Campaign> getAllCampaignsBySort(@RequestParam(required = false, defaultValue = "1") int sort) {
        if (sort == 1) {
            return campaignService.findAllByStartDate();
        } else if (sort == 2)
            return campaignService.findAllByDueDate();

        return null;
    }

}
