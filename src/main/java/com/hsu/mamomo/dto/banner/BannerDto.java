package com.hsu.mamomo.dto.banner;

import com.hsu.mamomo.domain.Banner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BannerDto {

    @NonNull
    private Banner banner;
}

