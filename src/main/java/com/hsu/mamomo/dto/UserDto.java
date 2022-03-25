package com.hsu.mamomo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z|가-힣]+[0-9]*$",message = "닉네임은 한글, 영문, 숫자만 가능합니다.")
    @Length(min = 2, max = 8,message = "닉네임은 길이가 2에서 8 사이여야 합니다.")
    private String nickname;

    @Pattern(regexp = "^[MF]$",message = "성별은 M 또는 F 중 하나여야 합니다.")
    private String sex;

    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Pattern(regexp = "^((19|20)\\d{2}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$",message = "날짜 형식은 [yyyy-mm-dd]입니다.")
    private String birth;
}
