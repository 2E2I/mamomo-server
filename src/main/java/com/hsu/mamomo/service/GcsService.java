package com.hsu.mamomo.service;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.hsu.mamomo.dto.GcsBannerImageDto;
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

    private final Storage storage;

    private String imgUrl;

    public String uploadFileToGCS(GcsBannerImageDto gcsBannerImageDto, MultipartFile file) {

        String bucketName = gcsBannerImageDto.getBucketName();
        String blobName = gcsBannerImageDto.getFilePath() + "/" + gcsBannerImageDto.getFileName();
        BlobId blobId = BlobId.of(bucketName, blobName);

        byte[] content = convertMultipartFileToByteArray(file);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
        try (WriteChannel writer = storage.writer(blobInfo)) {
            writer.write(
                    ByteBuffer.wrap(content, 0, content.length));
        } catch (IOException ex) {
            System.out.println("GcsService.uploadFileToGCS");
        }

        imgUrl = "https://storage.googleapis.com/" + bucketName + "/" + blobName + ".jpg";

        return imgUrl;
    }

    public String deleteFile(GcsBannerImageDto gcsBannerImageDto) {
        String bucketName = gcsBannerImageDto.getBucketName();
        String blobName = gcsBannerImageDto.getFilePath() + "/" + gcsBannerImageDto.getFileName();
        BlobId blobId = BlobId.of(bucketName, blobName);

        if(storage.delete(blobId)){
            imgUrl = "https://storage.googleapis.com/" + bucketName + "/" + blobName + ".jpg";
        } else {
            imgUrl = null;
        }

        return imgUrl;
    }


    public byte[] convertMultipartFileToByteArray(MultipartFile file) {
        byte[] byteArr = new byte[0];
        try {
            byteArr = file.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("GcsService.convertMultipartFileToByteArray ERROR");
        }

        return byteArr;
    }

}
