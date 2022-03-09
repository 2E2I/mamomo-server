package com.hsu.mamomo.controller;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.repository.CampaignSearchRepository;
import com.hsu.mamomo.service.CampaignSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CampaignSearchController {

    private final CampaignSearchService campaignSearchService;
    private final CampaignSearchRepository campaignSearchRepository;

    @GetMapping("/search")
    public List<Campaign> searchByTitleOrBody(@RequestParam(value = "keyword") String keyword,
                                              @RequestParam(value = "sort", defaultValue = "start_date,desc") String sort) {
        String[] _sort = sort.split(",");
        // sort = [field, direction]
        return campaignSearchService.searchByTitleOrBody(keyword, _sort[0], _sort[1]);
    }
}
