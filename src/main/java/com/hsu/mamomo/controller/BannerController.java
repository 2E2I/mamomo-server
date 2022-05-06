package com.hsu.mamomo.controller;

import com.hsu.mamomo.dto.BannerDto;
import com.hsu.mamomo.dto.GcsBannerImageDto;
import com.hsu.mamomo.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/banner")
@RestController
public class BannerController {

    private final BannerService bannerService;

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping
    public ResponseEntity<GcsBannerImageDto> saveBanner(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @ModelAttribute BannerDto bannerDto) {

        return bannerService.saveBanner(authorization, bannerDto);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @DeleteMapping
    public ResponseEntity<String> deleteBanner(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody GcsBannerImageDto gcsBannerImageDto) {
        return bannerService.deleteBanner(authorization,gcsBannerImageDto);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping
    public void getBannerList() {
        bannerService.getBannerList();
    }
}
