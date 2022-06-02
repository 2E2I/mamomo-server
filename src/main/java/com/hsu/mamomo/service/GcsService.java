package com.hsu.mamomo.service;

import static com.hsu.mamomo.controller.exception.ErrorCode.BANNER_NOT_FOUND;
import static com.hsu.mamomo.controller.exception.ErrorCode.FAIL_SAVE_BANNER;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.dto.banner.GcsFIleDto;
import java.io.IOException;
import java.nio.ByteBuffer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class GcsService {

    private final String GCS_BUCKET_URL = "https://storage.googleapis.com/";

    private final Storage storage;

    private String imgUrl;
    private byte[] byteArr;

    public String uploadFileToGCS(GcsFIleDto gcsBannerImageDto, MultipartFile file) {
        byte[] content = convertMultipartFileToByteArray(file);
        BlobInfo blobInfo = BlobInfo.newBuilder(getBlobId(gcsBannerImageDto))
                .setContentType("image/jpeg").build();
        try (WriteChannel writer = storage.writer(blobInfo)) {
            writer.write(
                    ByteBuffer.wrap(content, 0, content.length));
            imgUrl = getImgUrl(gcsBannerImageDto);
        } catch (IOException ex) {
            throw new CustomException(FAIL_SAVE_BANNER);
        }
        return imgUrl;
    }

    public void deleteFile(GcsFIleDto gcsBannerImageDto) {
        if (!storage.delete(getBlobId(gcsBannerImageDto))) {
            throw new CustomException(BANNER_NOT_FOUND);
        }
    }

    public BlobId getBlobId(GcsFIleDto gcsBannerImageDto) {
        String bucketName = getBucketName(gcsBannerImageDto);
        String blobName = getBlobName(gcsBannerImageDto);
        return BlobId.of(bucketName, blobName);
    }

    public String getBucketName(GcsFIleDto gcsBannerImageDto) {
        return gcsBannerImageDto.getBucketName();
    }

    public String getBlobName(GcsFIleDto gcsBannerImageDto) {
        return gcsBannerImageDto.getFilePath() + "/" + gcsBannerImageDto.getFileName();
    }

    public String getImgUrl(GcsFIleDto gcsBannerImageDto) {
        return GCS_BUCKET_URL + getBucketName(gcsBannerImageDto) + "/" + getBlobName(
                gcsBannerImageDto);
    }

    public byte[] convertMultipartFileToByteArray(MultipartFile file) {
        try {
            byteArr = file.getBytes();
        } catch (IOException e) {
            throw new CustomException(FAIL_SAVE_BANNER);
        }

        return byteArr;
    }

}
