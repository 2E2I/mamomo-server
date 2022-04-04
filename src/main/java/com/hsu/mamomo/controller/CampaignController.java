package com.hsu.mamomo.controller;

import static com.hsu.mamomo.controller.exception.ErrorCode.INVALID_JWT_TOKEN;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.service.CampaignService;
import com.hsu.mamomo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/campaigns")
@RestController
public class CampaignController {

    private final CampaignService campaignService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

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
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization) {

        String jwtToken = authorization.substring(7);

        // 유효한 토큰인지 검증
        if (!jwtTokenProvider.validateToken(jwtToken))
            throw new CustomException(INVALID_JWT_TOKEN);

        return campaignService.getCampaigns(sort, category_id, userService.getUserIdByJwtToken(jwtToken));
    }

}
