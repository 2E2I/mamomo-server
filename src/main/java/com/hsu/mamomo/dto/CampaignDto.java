package com.hsu.mamomo.dto;

import com.hsu.mamomo.domain.Campaign;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor
public class CampaignDto {
    @NonNull
    private List<Campaign> campaigns;
}
