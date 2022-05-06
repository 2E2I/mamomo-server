package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.BANNER_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.FAIL_SAVE_BANNER;
import static com.hsu.mamomo.controller.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.MISMATCH_JWT_USER;
import static com.hsu.mamomo.controller.exception.ErrorCode.UNAUTHORIZED;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Banner;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.BannerDto;
import com.hsu.mamomo.dto.GcsBannerImageDto;
import com.hsu.mamomo.jwt.LoginAuthenticationUtil;
import com.hsu.mamomo.repository.jpa.BannerRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BannerService {

    private final String BUCKET_NAME = "mamomo-banner-storage";

    private final BannerRepository bannerRepository;
    private final UserRepository userRepository;
    private final GcsService gcsService;
    private final LoginAuthenticationUtil loginAuthenticationUtil;

    private Banner banner;
    private GcsBannerImageDto gcsBannerImageDto;

    public ResponseEntity<GcsBannerImageDto> saveBanner(String authorization, BannerDto bannerDto) {
        String userId = bannerDto.getUserId();

        if (authorization == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        if (!loginAuthenticationUtil.getUserIdFromAuth(authorization).equals(userId)) {
            throw new CustomException(MISMATCH_JWT_USER);
        }

        Optional<User> user = userRepository.findUserById(userId);
        if (user.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        LocalDateTime now = LocalDateTime.now();
        String localDateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHss"));

        gcsBannerImageDto = GcsBannerImageDto.builder()
                .bucketName(BUCKET_NAME)
                .filePath(userId)
                .fileName(bannerDto.getCampaignId() + "_" + localDateTime)
                .build();

        // GCS에 배너 저장, 이미지 URL을 imgUrl 변수에 저장
        String imgUrl = gcsService.uploadFileToGCS(gcsBannerImageDto, bannerDto.getBannerImg());
        if (imgUrl == null) {
            throw new CustomException(FAIL_SAVE_BANNER);
        }

        banner = Banner.builder()
                .user(user.get())
                .imgUrl(imgUrl)
                .build();

        bannerRepository.save(banner);

        return new ResponseEntity<>(gcsBannerImageDto, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteBanner(String authorization,
            GcsBannerImageDto gcsBannerImageDto) {
        String imgUrl = gcsService.deleteFile(gcsBannerImageDto);
        if (imgUrl != null) {
            Optional<Banner> banner = bannerRepository.findBannerByImgUrl(imgUrl);
            if (banner.isEmpty()) {
                throw new CustomException(BANNER_NOT_FOUND);
            } else {
                bannerRepository.delete(banner.get());
            }
        } else {
            throw new CustomException(BANNER_NOT_FOUND);
        }
        return new ResponseEntity<>(gcsBannerImageDto.getFileName() + " 파일 삭제됨", HttpStatus.OK);
    }

    public void getBannerList() {

    }


}
