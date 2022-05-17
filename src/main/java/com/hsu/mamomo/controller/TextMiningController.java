package com.hsu.mamomo.controller;

import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.dto.TextDto;
import com.hsu.mamomo.dto.TextMiningCampaignDto;
import com.hsu.mamomo.service.TextMiningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/textMining")
@RestController
public class TextMiningController {

    private final TextMiningService textMiningService;

    @PostMapping
    public TextMiningCampaignDto textMining(
            @PageableDefault(size = 20) Pageable pageable,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestBody TextDto textDto) {
        return textMiningService.requestTextMining(pageable, authorization, textDto);
    }

}
