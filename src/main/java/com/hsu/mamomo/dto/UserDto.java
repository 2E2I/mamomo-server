package com.hsu.mamomo.dto;

import java.util.Date;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {

    private String email;

    private String password;

    private String nickname;

    private String profile;

    private String sex;

    private Date birth;
}
