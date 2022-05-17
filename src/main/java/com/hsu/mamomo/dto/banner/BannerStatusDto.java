package com.hsu.mamomo.dto.banner;

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
public class BannerStatusDto {

    @NonNull
    private String bannerId;

    @NonNull
    private String email;
}
