package com.hsu.mamomo.service;

import com.fasterxml.uuid.Generators;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.LoginDto;
import com.hsu.mamomo.dto.UserDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.repository.jpa.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public User signUp(UserDto userDto) {
        User user = User.builder()
                .id(Generators.randomBasedGenerator().generate().toString())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .sex(userDto.getSex())
                .birth(userDto.getBirth())
                .roles(Collections.singletonList("ROLE_USER")) // 최초 가입시 권한 USER
                .build();

        return userRepository.save(user);

    }

    public String authenticate(LoginDto loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new AuthenticationServiceException("가입되지 않은 E-MAIL 입니다."));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        return jwtTokenProvider.createToken(user.getUsername(), roles);
    }

    public UserDto getUserInfo(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            User user = userRepository.findByEmail(email).get();
            UserDto userInfo = UserDto.builder()
                    .email(user.getEmail())
                    .password("")
                    .profile(user.getProfile())
                    .sex(user.getSex())
                    .birth(user.getBirth())
                    .nickname(user.getNickname())
                    .profile(user.getProfile())
                    .build();
            return userInfo;
        } else {
            throw new UsernameNotFoundException("가입되지 않은 E-MAIL 입니다.");
        }
    }


}
