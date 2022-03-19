package com.hsu.mamomo.controller;

import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.UserDto;
import com.hsu.mamomo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    /**
     *
     * 회원가입
     * @param userDto
     * @return
     */
    @PostMapping("/user/signup")
    public ResponseEntity<User> signUp(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.signUp(userDto));
    }
}
