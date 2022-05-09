package com.hsu.mamomo.dto;

import com.hsu.mamomo.domain.FavTopic;
import java.time.LocalDate;
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
public class ProfileDto {

    private String profileImgUrl;
    private String nickname;
    private String sex;
    private LocalDate birth;
    private List<FavTopic> favTopics;
}
