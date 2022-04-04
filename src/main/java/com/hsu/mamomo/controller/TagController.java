package com.hsu.mamomo.controller;

import com.hsu.mamomo.dto.TagDto;
import com.hsu.mamomo.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/search")
@RestController
public class TagController {

    private final TagService campaignSearchService;

    @GetMapping
    public ResponseEntity<TagDto> searchTag() {
        return ResponseEntity.ok()
                .body(new TagDto(campaignSearchService.findTop10Tags()));
    }

}
