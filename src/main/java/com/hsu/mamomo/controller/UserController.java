package com.hsu.mamomo.controller;

import static com.hsu.mamomo.controller.exception.ErrorCode.MISMATCH_JWT_USER;
import static com.hsu.mamomo.controller.exception.ErrorCode.UNAUTHORIZED;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.dto.LoginDto;
import com.hsu.mamomo.dto.ProfileDto;
import com.hsu.mamomo.dto.ProfileModifyDto;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.dto.UserDto;
import com.hsu.mamomo.dto.UserInfoDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.jwt.LoginAuthenticationUtil;
import com.hsu.mamomo.service.UserService;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final LoginAuthenticationUtil loginAuthenticationUtil;

    /**
     * 회원가입
     *
     * @param userDto
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody @Valid UserDto userDto) {
        return userService.signUp(userDto);
    }

    /**
     * 로그인 -> jwt 토큰 발급
     *
     * @param loginDto
     * @return
     */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> signUp(@RequestBody LoginDto loginDto) {
        return userService.authenticate(loginDto);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/{email}")
    public UserInfoDto getUserInfo(@PathVariable String email,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization) {
        return userService.getUserInfo();
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        return userService.deleteUser(email);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping("/profile/{email}")
    public ResponseEntity<ProfileDto> updateProfile(@Valid @PathVariable("email") String email,
            @ModelAttribute ProfileModifyDto profileModifyDto,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        if (authorization == null) {
            throw new CustomException(UNAUTHORIZED);
        }
        if (!loginAuthenticationUtil.getUserEmailFromAuth(authorization).equals(email)) {
            throw new CustomException(MISMATCH_JWT_USER);
        }

        return userService.updateProfile(email, profileModifyDto);
    }

}
