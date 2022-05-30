package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.BANNER_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.DUPLICATE_BANNER;
import static com.hsu.mamomo.service.encoding.EncodingImage.getBase64EncodedImage;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.domain.Banner;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.banner.BannerDto;
import com.hsu.mamomo.dto.banner.BannerModifyDto;
import com.hsu.mamomo.dto.banner.BannerSaveDto;
import com.hsu.mamomo.dto.banner.BannerListDto;
import com.hsu.mamomo.dto.banner.BannerStatusDto;
import com.hsu.mamomo.dto.banner.GcsFIleDto;
import com.hsu.mamomo.jwt.LoginAuthenticationUtil;
import com.hsu.mamomo.repository.jpa.BannerRepository;
import com.hsu.mamomo.repository.jpa.UserRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BannerService {

    private final String BUCKET_NAME = "mamomo-banner-storage";

    private final BannerRepository bannerRepository;
    private final UserRepository userRepository;
    private final GcsService gcsService;
    private final LoginAuthenticationUtil loginAuthenticationUtil;


    @Transactional
    public ResponseEntity<BannerDto> saveBanner(String authorization,
            BannerSaveDto bannerSaveDto) {
        User user = loginAuthenticationUtil.getUserIdByEmail(authorization,
                bannerSaveDto.getEmail());
        String userId = user.getId();
        String bannerId = getBannerId();

        // check bannerId is duplicate
        checkBannerIdIsDuplicate(bannerId);

        // 원본 이미지
        GcsFIleDto originalGcsFIleDto = GcsFIleDto.builder()
                .bucketName(BUCKET_NAME)
                .filePath(userId)
                .fileName("original_" + bannerId)
                .build();

        // 배너 이미지
        GcsFIleDto gcsFIleDto = GcsFIleDto.builder()
                .bucketName(BUCKET_NAME)
                .filePath(userId)
                .fileName(bannerId)
                .build();

        // GCS에 배너 저장, 이미지 URL을 imgUrl 변수에 저장
        String originalImgUrl = gcsService.uploadFileToGCS(originalGcsFIleDto,
                bannerSaveDto.getOriginalImgData());
        String imgUrl = gcsService.uploadFileToGCS(gcsFIleDto, bannerSaveDto.getImgData());

        Banner banner = Banner.builder()
                .user(user)
                .bannerId(bannerId)
                .originalImg(originalImgUrl)
                .img(imgUrl)
                .url(bannerSaveDto.getUrl())
                .date(bannerSaveDto.getDate())
                .siteType(bannerSaveDto.getSiteType())
                .title(bannerSaveDto.getTitle())
                .info(bannerSaveDto.getInfo())
                .width(bannerSaveDto.getWidth())
                .height(bannerSaveDto.getHeight())
                .bgColor1(bannerSaveDto.getBgColor1())
                .bgColor2(bannerSaveDto.getBgColor2())
                .textColor1(bannerSaveDto.getTextColor1())
                .textColor2(bannerSaveDto.getTextColor2())
                .textColor3(bannerSaveDto.getTextColor3())
                .textFont1(bannerSaveDto.getTextFont1())
                .textFont2(bannerSaveDto.getTextFont2())
                .textFont3(bannerSaveDto.getTextFont3())
                .build();

        bannerRepository.save(banner);

        BannerDto bannerDto = BannerDto.builder()
                .banner(banner)
                .build();

        return new ResponseEntity<>(bannerDto, HttpStatus.OK);
    }

    public ResponseEntity<BannerDto> getBannerStatus(String authorization,
            BannerStatusDto bannerStatusDto) {

        User user = loginAuthenticationUtil.getUserIdByEmail(authorization,
                bannerStatusDto.getEmail());
        String userId = user.getId();

        Optional<Banner> bannerOptional = bannerRepository.findBannerByBannerId(
                bannerStatusDto.getBannerId());
        if (bannerOptional.isEmpty()) {
            throw new CustomException(BANNER_NOT_FOUND);
        } else {
            Banner banner = bannerOptional.get();

            // 이미지 URL 인코딩
            banner.setOriginalImg(getBase64EncodedImage(banner.getOriginalImg()));

            BannerDto bannerDto = BannerDto.builder()
                    .banner(banner)
                    .build();

            return new ResponseEntity<>(bannerDto, HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<BannerDto> modifyBanner(String authorization,
            BannerModifyDto bannerModifyDto) {

        User user = loginAuthenticationUtil.getUserIdByEmail(authorization,
                bannerModifyDto.getEmail());
        String userId = user.getId();

        Optional<Banner> bannerOptional = bannerRepository.findBannerByBannerId(
                bannerModifyDto.getBannerId());
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

            String imgUrl = gcsService.uploadFileToGCS(gcsFIleDto, bannerModifyDto.getImgData());

            banner.setImg(imgUrl);
            banner.setDate(bannerModifyDto.getDate());
            banner.setSiteType(bannerModifyDto.getSiteType());
            banner.setTitle(bannerModifyDto.getTitle());
            banner.setInfo(bannerModifyDto.getInfo());
            banner.setWidth(bannerModifyDto.getWidth());
            banner.setHeight(bannerModifyDto.getHeight());
            banner.setBgColor1(bannerModifyDto.getBgColor1());
            banner.setBgColor2(bannerModifyDto.getBgColor2());
            banner.setTextColor1(bannerModifyDto.getTextColor1());
            banner.setTextColor2(bannerModifyDto.getTextColor2());
            banner.setTextColor3(bannerModifyDto.getTextColor3());
            banner.setTextFont1(bannerModifyDto.getTextFont1());
            banner.setTextFont2(bannerModifyDto.getTextFont2());
            banner.setTextFont3(bannerModifyDto.getTextFont3());

            BannerDto bannerDto = BannerDto.builder()
                    .banner(banner)
                    .build();

            return new ResponseEntity<>(bannerDto, HttpStatus.OK);
        }
    }

    public BannerListDto getBannerListByUser(Pageable pageable, String authorization,
            String email) {
        // 배너 유무 검사
        User user = loginAuthenticationUtil.getUserIdByEmail(authorization, email);
        if (bannerRepository.findBannersByUser(user).isEmpty()) {
            throw new CustomException(BANNER_NOT_FOUND);
        }

        Page<Banner> banners = bannerRepository.findByUser(user, pageable);

        // 이미지 URL 인코딩
        banners.map(banner -> {
            banner.setOriginalImg(getBase64EncodedImage(banner.getOriginalImg()));
            return banner;
        });

        return new BannerListDto(banners);
    }

    public BannerListDto getBannerList(Pageable pageable) {
        // 배너 유무 검사
        if (bannerRepository.findAll().isEmpty()) {
            throw new CustomException(BANNER_NOT_FOUND);
        }
        Page<Banner> banners = bannerRepository.findAll(pageable);

        // 이미지 URL 인코딩
        banners.map(banner -> {
            banner.setOriginalImg(getBase64EncodedImage(banner.getOriginalImg()));
            return banner;
        });

        return new BannerListDto(banners);
    }

    @Transactional
    public ResponseEntity<String> deleteBanner(String email, String bannerId) {
        Optional<Banner> banner = bannerRepository.findBannerByBannerId(bannerId);
        String userId = userRepository.findByEmail(email).get().getId();
        String originalImgUrl = banner.get().getOriginalImg().split(userId + "/")[1];

        if (banner.isEmpty()) {
            throw new CustomException(BANNER_NOT_FOUND);
        } else {
            bannerRepository.delete(banner.get());

            gcsService.deleteFile(GcsFIleDto.builder()
                    .bucketName(BUCKET_NAME)
                    .filePath(userId)
                    .fileName(bannerId)
                    .build());

            gcsService.deleteFile(GcsFIleDto.builder()
                    .bucketName(BUCKET_NAME)
                    .filePath(userId)
                    .fileName(originalImgUrl)
                    .build());
        }

        return new ResponseEntity<>(bannerId + " 파일 삭제됨", HttpStatus.OK);
    }

    private String getBannerId() {
        LocalDateTime now = LocalDateTime.now();
        String localDateTime = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHss"));
        return localDateTime;
    }

    public void checkBannerIdIsDuplicate(String bannerId) {
        Optional<Banner> banner = bannerRepository.findBannerByBannerId(bannerId);
        if (banner.isPresent()) {
            throw new CustomException(DUPLICATE_BANNER);
        }
    }
}
