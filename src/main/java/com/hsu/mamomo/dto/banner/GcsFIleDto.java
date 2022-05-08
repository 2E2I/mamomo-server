package com.hsu.mamomo.dto.banner;

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
public class GcsFIleDto {

    private String bucketName;
    private String filePath;
    private String fileName;
}
