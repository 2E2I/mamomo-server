package com.hsu.mamomo.controller;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.dto.TagDto;
import com.hsu.mamomo.service.CampaignSearchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<TagDto> searchTag() {
        return ResponseEntity.ok()
                .body(new TagDto(campaignSearchService.findTop10Tags()));
    }

    @GetMapping("/campaigns")
    public ResponseEntity<CampaignDto> searchByTitleOrBody(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "sort", defaultValue = "none,none") String sort) {
        String[] _sort = sort.split(",");
        // sort = [field, direction]
        return ResponseEntity.ok()
                .body(new CampaignDto(
                        campaignSearchService.searchByTitleOrBody(keyword, _sort[0], _sort[1])));
    }

}
