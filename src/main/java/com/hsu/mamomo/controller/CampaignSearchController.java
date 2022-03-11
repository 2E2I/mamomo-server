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
@RequestMapping("/api")
@RestController
public class CampaignSearchController {

    private final CampaignSearchService campaignSearchService;

    @GetMapping("/search")
    public Map<String, List<String>> searchTag() {

        Map<String, List<String>> result = new HashMap<>();

        result.put("top_10_tags", campaignSearchService.findTop10Tags());

        return result;
    }

    @GetMapping("/search/campaigns")
    public List<Campaign> searchByTitleOrBody(@RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "sort", defaultValue = "none,none") String sort) {
        String[] _sort = sort.split(",");
        // sort = [field, direction]
        return campaignSearchService.searchByTitleOrBody(keyword, _sort[0], _sort[1]);
    }

}
