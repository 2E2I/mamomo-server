package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.BANNER_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.MISMATCH_JWT_USER;
import static com.hsu.mamomo.controller.exception.ErrorCode.UNAUTHORIZED;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Banner;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.banner.BannerDto;
import com.hsu.mamomo.dto.banner.BannerSaveDto;
import com.hsu.mamomo.dto.banner.BannerListDto;
import com.hsu.mamomo.dto.banner.GcsFIleDto;
import com.hsu.mamomo.jwt.LoginAuthenticationUtil;
import com.hsu.mamomo.repository.jpa.BannerRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import com.hsu.mamomo.service.encoding.EncodingImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    private GcsFIleDto gcsBannerImageDto;
    private List<BannerDto> bannerList = new ArrayList<>();

    public ResponseEntity<BannerDto> saveBanner(String authorization,
            BannerSaveDto bannerSaveDto) {
        if (authorization == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        Optional<User> user = userRepository.findByEmail(bannerSaveDto.getEmail());

        if (user.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        String userId = user.get().getId();

        if (!loginAuthenticationUtil.getUserIdFromAuth(authorization).equals(userId)) {
            throw new CustomException(MISMATCH_JWT_USER);
        }

        LocalDateTime now = LocalDateTime.now();
        String localDateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHss"));
        String bannerId = bannerSaveDto.getCampaignId() + "_" + localDateTime;

        gcsBannerImageDto = GcsFIleDto.builder()
                .bucketName(BUCKET_NAME)
                .filePath(userId)
                .fileName(bannerId)
                .build();

        // GCS에 배너 저장, 이미지 URL을 imgUrl 변수에 저장
        String imgUrl = gcsService.uploadFileToGCS(gcsBannerImageDto,
                bannerSaveDto.getBannerImg());

        banner = Banner.builder()
                .user(user.get())
                .bannerId(bannerId)
                .imgUrl(imgUrl)
                .build();

        bannerRepository.save(banner);

        BannerDto bannerDto = BannerDto.builder()
                .bannerId(banner.getBannerId())
                .imgUrl(banner.getImgUrl())
                .build();

        return new ResponseEntity<>(bannerDto, HttpStatus.OK);
    }

    public ResponseEntity<String> deleteBanner(String email, String bannerId) {

        Optional<Banner> banner = bannerRepository.findBannerByBannerId(bannerId);
        String userId = userRepository.findByEmail(email).get().getId();
        gcsService.deleteFile(userId, bannerId);

        if (banner.isEmpty()) {
            throw new CustomException(BANNER_NOT_FOUND);
        } else {
            bannerRepository.delete(banner.get());
        }

        return new ResponseEntity<>(bannerId + " 파일 삭제됨", HttpStatus.OK);
    }

    public BannerListDto getBannerList(String authorization, String email) {
        if (authorization == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new CustomException(MEMBER_NOT_FOUND);
        }

        String userId = user.get().getId();

        if (!loginAuthenticationUtil.getUserIdFromAuth(authorization).equals(userId)) {
            throw new CustomException(MISMATCH_JWT_USER);
        }

        List<Banner> banners = bannerRepository.findBannersByUser(user.get()).orElse(null);
        if (banners == null) {
            throw new CustomException(BANNER_NOT_FOUND);
        }

        banners.forEach(banner -> {
            BannerDto bannerDto = BannerDto.builder()
                    .bannerId(banner.getBannerId())
                    .imgUrl(EncodingImage.getBase64EncodedImage(banner.getImgUrl()))
                    .build();
            bannerList.add(bannerDto);
        });

        BannerListDto bannerListDto = BannerListDto.builder()
                .bannerList(bannerList)
                .build();

        return bannerListDto;
    }


}
