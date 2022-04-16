package com.hsu.mamomo.controller;

import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.dto.TagDto;
import com.hsu.mamomo.service.CampaignService;
import com.hsu.mamomo.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class TagController {

    private final TagService tagService;
    private final CampaignService campaignService;

    @GetMapping("/search")
    public ResponseEntity<TagDto> getTags(
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "to", defaultValue = "1000") Integer to
    ) {
        return ResponseEntity.ok()
                .body(new TagDto(tagService.getRangeTags(from, to)));
    }

    @GetMapping("/tag/{tagName}")
    public CampaignDto searchByTag(
            @PathVariable String tagName,
            @PageableDefault(size = 20, sort = "start_date", direction = Direction.DESC) Pageable pageable,
            @RequestParam(defaultValue = "false") Boolean heart,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization
    ) {
        return campaignService.getCampaigns(pageable, null, null, heart, authorization, tagName);
    }

}
