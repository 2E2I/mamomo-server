package com.hsu.mamomo.dto.banner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BannerSaveDto {

    @NonNull
    private MultipartFile bannerImg;

    @NonNull
    private String email;

    @NonNull
    private String campaignId;
}
