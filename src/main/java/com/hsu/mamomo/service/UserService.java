package com.hsu.mamomo.service;

import com.fasterxml.uuid.Generators;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.UserDto;
import com.hsu.mamomo.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signUp(UserDto userDto) {
        User user = User.builder()
                .id(Generators.randomBasedGenerator().generate())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .nickname(userDto.getNickname())
                .sex(userDto.getSex())
                .birth(userDto.getBirth())
                .build();

        return userRepository.save(user);

    }
}
