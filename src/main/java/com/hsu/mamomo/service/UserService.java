package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.DUPLICATE_EMAIL;
import static com.hsu.mamomo.controller.exception.ErrorCode.DUPLICATE_NICKNAME;
import static com.hsu.mamomo.controller.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.TOPIC_NOT_FOUND;

import com.fasterxml.uuid.Generators;
import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Authority;
import com.hsu.mamomo.domain.FavTopic;
import com.hsu.mamomo.domain.Topic;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.LoginDto;
import com.hsu.mamomo.dto.ProfileDto;
import com.hsu.mamomo.dto.ProfileModifyDto;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.dto.UserDto;
import com.hsu.mamomo.dto.UserInfoDto;
import com.hsu.mamomo.dto.banner.GcsFIleDto;
import com.hsu.mamomo.jwt.JwtAuthenticationFilter;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.jwt.SecurityUtil;
import com.hsu.mamomo.repository.jpa.TopicRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private final String BUCKET_NAME = "mamomo-profile-storage";
    private final String DEFAULT_IMG_URL = "https://storage.googleapis.com/mamomo-profile-storage/default.png";
    private final GcsService gcsService;

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
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

        String user_id = Generators.randomBasedGenerator().generate().toString();
        User user = User.builder()
                .id(user_id)
                .email(userDto.getEmail())
                .profileImgUrl(DEFAULT_IMG_URL)
                .password(passwordEncoder.encode(userDto.getPassword()))
                .nickname(userDto.getNickname())
                .sex(userDto.getSex())
                .birth(LocalDate.parse(userDto.getBirth()))
                .authorities(Collections.singleton(authority)) // 최초 가입시 권한 USER
                .build();

        /*
         * 회원가입 요청에 관심 기부 분야 리스트가 있으면 저장
         * */
        if (userDto.getFavTopics() != null) {
            List<FavTopic> favTopics = new ArrayList<>();
            userDto.getFavTopics().forEach(
                    topicId -> {
                        Optional<Topic> topic = topicRepository.findTopicById(topicId);
                        if (topic.isPresent()) {
                            FavTopic favTopic = FavTopic.builder()
                                    .user(user)
                                    .topic(topic.get())
                                    .build();
                            favTopics.add(favTopic);
                        } else {
                            throw new CustomException(TOPIC_NOT_FOUND);
                        }
                    }
            );
            user.setFavTopic(favTopics);
        }

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

        Optional<User> userOpt = userRepository.findByEmail(loginDto.getEmail());
        if (userOpt.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        User user = userOpt.get();
        ProfileDto profile = ProfileDto.builder()
                // 회원가입 시에는 default img로 설정
                .profileImgUrl(user.getProfileImgUrl())
                .nickname(user.getNickname())
                .sex(user.getSex())
                .birth(user.getBirth())
                .favTopics(user.getFavTopic())
                .build();

        return new ResponseEntity<>(
                TokenDto.builder()
                .token(jwt)
                .profile(profile)
                .build(),
                httpHeaders, HttpStatus.OK);
    }

    public UserInfoDto getUserInfo() {
        log.info("현재 로그인 된 유저 = {}", SecurityUtil.getCurrentUsername());
        Optional<User> user = SecurityUtil.getCurrentUsername()
                .flatMap(userRepository::findOneWithAuthoritiesByEmail);
        if (user.isPresent()) {
            return new UserInfoDto(user.get());
        } else {
            throw new CustomException(MEMBER_NOT_FOUND);
        }
    }

    public ResponseEntity<String> deleteUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        } else {
            throw new CustomException(MEMBER_NOT_FOUND);
        }
        return new ResponseEntity<>(email + " 유저 삭제됨", HttpStatus.OK);
    }

    public ResponseEntity<ProfileDto> updateProfile(String email, ProfileModifyDto profileModifyDto) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        User user = userOpt.get();

        Map<String, ProfileDto> result = new HashMap<>();
        ProfileDto previous = convertUserToProfileDto(user);

        // GCS에 프로필 이미지 저장
        // filepath : /userEmail/userEmail
        GcsFIleDto gcsFIleDto = GcsFIleDto.builder()
                .bucketName(BUCKET_NAME)
                .filePath(user.getEmail())
                .fileName(user.getEmail())
                .build();

        String imgUrl = gcsService.uploadFileToGCS(gcsFIleDto, profileModifyDto.getProfileImg());

        if (profileModifyDto.getProfileImg() != null) {
            user.setProfileImgUrl(imgUrl);
        }
        if (profileModifyDto.getNickname() != null) {
            user.setNickname(profileModifyDto.getNickname());
        }
        if (profileModifyDto.getBirth() != null) {
            user.setBirth(profileModifyDto.getBirth());
        }
        if (profileModifyDto.getSex() != null) {
            user.setSex(profileModifyDto.getSex());
        }
        if (profileModifyDto.getFavTopics() != null) {
            List<FavTopic> favTopics = new ArrayList<>();
            profileModifyDto.getFavTopics().forEach(
                    topicId -> {
                        Optional<Topic> topic = topicRepository.findTopicById(topicId);
                        if (topic.isPresent()) {
                            FavTopic favTopic = FavTopic.builder()
                                    .user(user)
                                    .topic(topic.get())
                                    .build();
                            favTopics.add(favTopic);
                        } else {
                            throw new CustomException(TOPIC_NOT_FOUND);
                        }
                    }
            );
            user.getFavTopic().clear();
            user.getFavTopic().addAll(favTopics);
        }

        LocalDateTime modifiedAt = LocalDateTime.now();
        user.setModify_date(modifiedAt);

        userRepository.save(user);

        return ResponseEntity.ok().body(convertUserToProfileDto(user));
    }

    public ProfileDto convertUserToProfileDto(User user) {
        return ProfileDto.builder()
                .profileImgUrl(user.getProfileImgUrl())
                .nickname(user.getNickname())
                .birth(user.getBirth())
                .sex(user.getSex())
                .favTopics(user.getFavTopic())
                .build();
    }

}
