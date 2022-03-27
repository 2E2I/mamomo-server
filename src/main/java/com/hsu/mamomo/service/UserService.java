package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.DUPLICATE_EMAIL;
import static com.hsu.mamomo.controller.exception.ErrorCode.DUPLICATE_NICKNAME;

import com.fasterxml.uuid.Generators;
import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Authority;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.LoginDto;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.dto.UserDto;
import com.hsu.mamomo.jwt.JwtAuthenticationFilter;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.jwt.SecurityUtil;
import com.hsu.mamomo.repository.jpa.UserRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public ResponseEntity<UserDto> signUp(UserDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).orElse(null) != null) {
            throw new CustomException(DUPLICATE_EMAIL);
        }
        if (userRepository.findByNickname(userDto.getNickname()).orElse(null) != null) {
            throw new CustomException(DUPLICATE_NICKNAME);
        }

        Authority authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        User user = User.builder()
                .id(Generators.randomBasedGenerator().generate().toString())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .sex(userDto.getSex())
                .birth(LocalDate.parse(userDto.getBirth()))
                .authorities(Collections.singleton(authority)) // 최초 가입시 권한 USER
                .build();

        userRepository.save(user);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
        return new ResponseEntity<>(userDto, httpHeaders, HttpStatus.CREATED);
    }

    public ResponseEntity<TokenDto> authenticate(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),
                        loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.createToken(authentication);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtAuthenticationFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new TokenDto(jwt), httpHeaders, HttpStatus.OK);
    }

    public Optional<User> getUserInfo() {
        log.info("현재 로그인 된 유저 = {}", SecurityUtil.getCurrentUsername());
        return SecurityUtil.getCurrentUsername()
                .flatMap(userRepository::findOneWithAuthoritiesByEmail);
    }

    /*
     * JWT 토큰을 디코딩 하여 유저 정보와 일치하는지 확인
     * */
    public Boolean isEqualUserTokenInfoAndUserInfo(String token, String userId) {
        String userEmail = userRepository.findUserById(userId).get().getEmail();
        return jwtTokenProvider.getUserPk(token).equals(userEmail);
    }

}
