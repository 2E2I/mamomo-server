package com.hsu.mamomo.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private String email;

    private String password;

    private String nickname;

    private String profile;

    private String sex;

    private LocalDate birth;
}
