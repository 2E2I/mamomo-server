package com.hsu.mamomo.controller;

import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.LoginDto;
import com.hsu.mamomo.dto.UserDto;
import com.hsu.mamomo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    /**
     *
     * 회원가입
     * @param userDto
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.signUp(userDto));
    }

    @PostMapping("/authenticate")
    public String signUp(@RequestBody LoginDto loginDto) {
        return userService.authenticate(loginDto);
    }

    @GetMapping("/{email}")
    public UserDto getUserInfo(@PathVariable String email) {
        log.info("{} 의 정보를 찾습니다 ..", email);
        return userService.getUserInfo(email);
    }
}
