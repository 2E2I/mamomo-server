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
    private final String BUCKET_NAME = "mamomo-banner-storage";

    private final Storage storage;

    private String imgUrl;
    private byte[] byteArr;

    public String uploadFileToGCS(GcsFIleDto gcsBannerImageDto, MultipartFile file) {
        String bucketName = gcsBannerImageDto.getBucketName();
        String blobName = gcsBannerImageDto.getFilePath() + "/" + gcsBannerImageDto.getFileName();
        BlobId blobId = BlobId.of(bucketName, blobName);

        byte[] content = convertMultipartFileToByteArray(file);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
        try (WriteChannel writer = storage.writer(blobInfo)) {
            writer.write(
                    ByteBuffer.wrap(content, 0, content.length));
            imgUrl = GCS_BUCKET_URL + bucketName + "/" + blobName;
        } catch (IOException ex) {
            throw new CustomException(FAIL_SAVE_BANNER);
        }
        return imgUrl;
    }

    public void deleteFile(String userId, String bannerId) {
        String blobName = userId + "/" + bannerId;
        BlobId blobId = BlobId.of(BUCKET_NAME, blobName);

        if (!storage.delete(blobId)) {
            throw new CustomException(BANNER_NOT_FOUND);
        }
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
