package com.hsu.mamomo.dto;

import com.hsu.mamomo.domain.User;
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
public class UserInfoDto {

    @NonNull
    private User user;
}
