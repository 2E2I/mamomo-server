package com.hsu.mamomo.controller;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.service.CampaignService;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping("/campaigns")
    public Map<String, List<Campaign>> getAllCampaigns(
            @RequestParam(value = "sort", defaultValue = "start_date,desc", required = false) String sort,
            @RequestParam(value = "category", required = false) String category) {

        Map<String, List<Campaign>> result = new HashMap<>();

        String[] _sort = sort.split(",");
        // sort = [field, direction]

        if (category != null) {
            category = category.replace("|", "%7C"); // 특수 문자 처리
            result.put("campaigns",
                    campaignService.findAllOfCategory(_sort[0], _sort[1], category));
        } else {
            result.put("campaigns", campaignService.findAll(_sort[0], _sort[1]));
        }

        return result;

    }

}
