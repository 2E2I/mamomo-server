package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.BANNER_NOT_FOUND;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Banner;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.banner.BannerDto;
import com.hsu.mamomo.dto.banner.BannerModifyDto;
import com.hsu.mamomo.dto.banner.BannerSaveDto;
import com.hsu.mamomo.dto.banner.BannerListDto;
import com.hsu.mamomo.dto.banner.GcsFIleDto;
import com.hsu.mamomo.jwt.LoginAuthenticationUtil;
import com.hsu.mamomo.repository.jpa.BannerRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import com.hsu.mamomo.service.encoding.EncodingImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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

    public ResponseEntity<BannerDto> saveBanner(String authorization,
            BannerSaveDto bannerSaveDto) {
        User user = loginAuthenticationUtil.getUserIdByEmail(authorization,
                bannerSaveDto.getEmail());
        String userId = user.getId();
        String bannerId = getBannerId();

        GcsFIleDto gcsFIleDto = GcsFIleDto.builder()
                .bucketName(BUCKET_NAME)
                .filePath(userId)
                .fileName(bannerId)
                .build();

        // GCS에 배너 저장, 이미지 URL을 imgUrl 변수에 저장
        String imgUrl = gcsService.uploadFileToGCS(gcsFIleDto, bannerSaveDto.getBannerImg());

        Banner banner = Banner.builder()
                .user(user)
                .bannerId(bannerId)
                .imgUrl(imgUrl)
                .date(bannerSaveDto.getDate())
                .build();

        bannerRepository.save(banner);

        BannerDto bannerDto = BannerDto.builder()
                .banner(banner)
                .build();

        return new ResponseEntity<>(bannerDto, HttpStatus.OK);
    }

    public BannerListDto getBannerListByUser(Pageable pageable, String authorization,
            String email) {
        User user = loginAuthenticationUtil.getUserIdByEmail(authorization, email);
        String userId = user.getId();

        List<Banner> banners = bannerRepository.findBannersByUser(user).orElse(null);
        if (banners == null) {
            throw new CustomException(BANNER_NOT_FOUND);
        }

        return new BannerListDto(
                bannerRepository.findByUser(user, pageable)
                        .map(banner -> {
                            banner.setImgUrl(
                                    EncodingImage.getBase64EncodedImage(banner.getImgUrl()));
                            return banner;
                        }));

    }

    public BannerListDto getBannerList(Pageable pageable) {

        return new BannerListDto(
                bannerRepository.findAll(pageable)
                        .map(banner -> {
                            banner.setImgUrl(
                                    EncodingImage.getBase64EncodedImage(banner.getImgUrl()));
                            return banner;
                        }));

    }

    public ResponseEntity<String> deleteBanner(String email, String bannerId) {

        Optional<Banner> banner = bannerRepository.findBannerByBannerId(bannerId);
        String userId = userRepository.findByEmail(email).get().getId();
        gcsService.deleteFile(GcsFIleDto.builder()
                .bucketName(BUCKET_NAME)
                .filePath(userId)
                .fileName(bannerId)
                .build());

        if (banner.isEmpty()) {
            throw new CustomException(BANNER_NOT_FOUND);
        } else {
            bannerRepository.delete(banner.get());
        }

        return new ResponseEntity<>(bannerId + " 파일 삭제됨", HttpStatus.OK);
    }

    public ResponseEntity<BannerDto> modifyBanner(String authorization, BannerModifyDto bannerModifyDto) {

        User user = loginAuthenticationUtil.getUserIdByEmail(authorization, bannerModifyDto.getEmail());
        String userId = user.getId();

        Optional<Banner> bannerOptional = bannerRepository.findBannerByBannerId(bannerModifyDto.getBannerId());
        if (bannerOptional.isEmpty()) {
            throw new CustomException(BANNER_NOT_FOUND);
        } else {
            Banner banner = bannerOptional.get();
            String bannerId = bannerModifyDto.getBannerId();
            GcsFIleDto gcsFIleDto = GcsFIleDto.builder()
                    .bucketName(BUCKET_NAME)
                    .filePath(userId)
                    .fileName(bannerId)
                    .build();

            gcsService.deleteFile(gcsFIleDto);

            String imgUrl = gcsService.uploadFileToGCS(gcsFIleDto,
                    bannerModifyDto.getBannerImg());

            banner.setImgUrl(imgUrl);
            banner.setDate(bannerModifyDto.getDate());

            BannerDto bannerDto = BannerDto.builder()
                    .banner(banner)
                    .build();

            return new ResponseEntity<>(bannerDto, HttpStatus.OK);
        }
    }

    private String getBannerId() {
        LocalDateTime now = LocalDateTime.now();
        String localDateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHss"));
        return localDateTime;
    }

}
