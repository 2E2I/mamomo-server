package com.hsu.mamomo.controller;

import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.service.CampaignService;
import com.hsu.mamomo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RequestMapping("/api/campaigns")
@RestController
public class CampaignController {

    private final CampaignService campaignService;
    private final UserService userService;

    @GetMapping
    public CampaignDto getAllCampaigns(
            @RequestParam(value = "sort", defaultValue = "start_date,desc", required = false) String sort,
            @RequestParam(value = "category", required = false) Integer category_id) {

        return campaignService.getCampaigns(sort, category_id, "");
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping
    public CampaignDto getAllCampaignsWithUserHeart(
            @RequestParam(value = "sort", defaultValue = "start_date,desc", required = false) String sort,
            @RequestParam(value = "category", required = false) Integer category_id,
            @RequestParam(value = "userId") String userId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization) {

        if (!userService.isEqualUserTokenInfoAndUserInfo(authorization.substring(7), userId)) {
            throw new RuntimeException();
        }

        return campaignService.getCampaigns(sort, category_id, userId);
    }

}
