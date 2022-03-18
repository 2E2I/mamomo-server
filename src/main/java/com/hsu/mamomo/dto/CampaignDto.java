package com.hsu.mamomo.dto;

import com.hsu.mamomo.domain.Campaign;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CampaignDto {
    private List<Campaign> campaigns;
}
