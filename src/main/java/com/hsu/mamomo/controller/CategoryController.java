package com.hsu.mamomo.controller;

import com.hsu.mamomo.service.CampaignSearchService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryController {

    private final CampaignSearchService campaignSearchService;
    private static final List<String> categoryList
            = List.of("아동|청소년",
            "어르신",
            "장애인",
            "어려운이웃",
            "다문화",
            "지구촌",
            "가족|여성",
            "우리사회",
            "동물",
            "환경");

    @GetMapping
    public Map<String, List<String>> getCategories() {
        Map<String, List<String>> result = new HashMap<>();
        result.put("categories",categoryList);

        return result;
    }
}
