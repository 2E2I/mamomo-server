package com.hsu.mamomo.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignInfoDto {

    private String id;

    private String siteType;

    private String url;

    private String title;

    private List<String> category;

    private List<String> tags;

    private String body;

    private String organizationName;

    private String thumbnail;

    private LocalDate dueDate;

    private LocalDate startDate;

    private Long targetPrice;

    private Long statusPrice;

    private Integer percent;

}
