package com.hsu.mamomo.dto;

import com.hsu.mamomo.domain.Campaign;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@NoArgsConstructor
public class TextMiningCampaignDto {

    @NonNull
    private Page<Campaign> campaigns;

    @NonNull
    private List<Map<String,String>> textMining;
}
