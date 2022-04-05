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
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.SearchPage;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor
public class CampaignDto {
    @NonNull
    private Page<Campaign> campaigns;
}
