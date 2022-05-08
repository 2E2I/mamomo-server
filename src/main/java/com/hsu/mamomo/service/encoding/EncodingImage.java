package com.hsu.mamomo.service.encoding;

import static com.hsu.mamomo.controller.exception.ErrorCode.FAIL_ENCODING;

import com.hsu.mamomo.controller.exception.CustomException;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class EncodingImage {

    // Image base64 encoding
    public static String getBase64EncodedImage(String imageURL) {
        try {
            return Base64.encodeBase64String(IOUtils.toByteArray(new URL(imageURL).openStream()));
        } catch (IOException e) {
            throw new CustomException(FAIL_ENCODING);
        }
    }
}
