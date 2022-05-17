package com.hsu.mamomo.controller;

import com.hsu.mamomo.dto.banner.BannerDto;
import com.hsu.mamomo.dto.banner.BannerModifyDto;
import com.hsu.mamomo.dto.banner.BannerSaveDto;
import com.hsu.mamomo.dto.banner.BannerListDto;
import com.hsu.mamomo.dto.banner.BannerStatusDto;
import com.hsu.mamomo.service.BannerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<BannerDto> saveBanner(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @ModelAttribute BannerSaveDto bannerSaveDto) {

        return bannerService.saveBanner(authorization, bannerSaveDto);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/status")
    public ResponseEntity<BannerDto> getBannerStatus(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @ModelAttribute BannerStatusDto bannerStatusDto) {

        return bannerService.getBannerStatus(authorization, bannerStatusDto);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/modify")
    public ResponseEntity<BannerDto> modifyBanner(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @ModelAttribute BannerModifyDto bannerModifyDto) {

        return bannerService.modifyBanner(authorization, bannerModifyDto);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/{email}")
    public BannerListDto getBannerListByUser(
            @PageableDefault(size = 20, sort = "date", direction = Direction.DESC) Pageable pageable,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @PathVariable String email) {

        return bannerService.getBannerListByUser(pageable, authorization, email);
    }

    @GetMapping
    public BannerListDto getBannerList(
            @PageableDefault(size = 20, sort = "date", direction = Direction.DESC) Pageable pageable) {

        return bannerService.getBannerList(pageable);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @DeleteMapping("/{email}/{bannerId}")
    public ResponseEntity<String> deleteBanner(
            @PathVariable String email,
            @PathVariable String bannerId) {

        return bannerService.deleteBanner(email, bannerId);
    }
}
