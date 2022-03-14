package com.hsu.mamomo.controller;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.service.CampaignSearchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/search")
@RestController
public class CampaignSearchController {

    private final CampaignSearchService campaignSearchService;

    @GetMapping
    public Map<String, List<String>> searchTag() {

        Map<String, List<String>> result = new HashMap<>();

        result.put("top_10_tags", campaignSearchService.findTop10Tags());

        return result;
    }

    @GetMapping("/campaigns")
    public Map<String, List<Campaign>> searchByTitleOrBody(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "sort", defaultValue = "none,none") String sort) {
        Map<String, List<Campaign>> result = new HashMap<>();

        String[] _sort = sort.split(",");
        // sort = [field, direction]
        result.put("campaigns",
                campaignSearchService.searchByTitleOrBody(keyword, _sort[0], _sort[1]));

        return result;
    }

}
