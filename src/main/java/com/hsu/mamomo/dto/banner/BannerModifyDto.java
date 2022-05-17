package com.hsu.mamomo.dto.banner;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BannerModifyDto {

    @NonNull
    private String bannerId;

    @NonNull
    private String email;

    @NonNull
    private MultipartFile imgData;

    @NonNull
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;

    @NonNull
    private String siteType;

    @NonNull
    private String title;

    @NonNull
    private String info;

    @NonNull
    private String width;

    @NonNull
    private String height;

    @NonNull
    private String bgColor1;

    @NonNull
    private String bgColor2;

    @NonNull
    private String textColor1;

    @NonNull
    private String textColor2;

    @NonNull
    private String textColor3;

    @NonNull
    private String textFont1;

    @NonNull
    private String textFont2;

    @NonNull
    private String textFont3;
}
