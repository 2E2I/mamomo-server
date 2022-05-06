package com.hsu.mamomo.controller;

import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.mamomo.dto.GcsBannerImageDto;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class BannerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static private String jwtToken;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String userId;
    private String campaignId;
    private MockMultipartFile multipartFile;
    private GcsBannerImageDto gcsBannerImageDto;

    @BeforeEach
    void authenticate() throws Exception {

        Map<String, String> input = new HashMap<>();
        input.put("email", "test@gmail.com");
        input.put("password", "password");

        MvcResult mvcResult = mockMvc
                .perform(RestDocumentationRequestBuilders.post("/api/user/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn();

        // 응답 바디의 jwt 토큰 추출
        String responseBody = mvcResult.getResponse().getContentAsString();
        TokenDto tokenDto = objectMapper.readValue(responseBody, TokenDto.class);
        jwtToken = tokenDto.getToken();

        // 발급된 토큰이 유효한 jwt 토큰인지 확인
        assertTrue(jwtTokenProvider.validateToken(tokenDto.getToken()));
    }

    @BeforeEach
    public void setBannerDto() throws IOException {
        userId = "dbb485f4-f588-4bfe-b485-f4f5885bfe9d";
        campaignId = "824";
        multipartFile = new MockMultipartFile("bannerImg",
                "test.jpg",
                "image/jpeg",
                new FileInputStream("src/test/resources/bannerTest.jpg"));
    }

    @DisplayName("배너 저장하기 테스트 - 성공 :: ")
    @Test
    public void saveBanner() throws Exception {
        mockMvc.perform(multipart("/api/banner")
                        .file(multipartFile)
                        .param("userId", userId)
                        .param("campaignId", campaignId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))

                .andExpect(status().isOk())

                // document
                .andDo(document("banner-save",
                        getDocumentRequest(),
                        getDocumentResponse(),

                        // 응답 필드 문서화
                        responseFields(
                                fieldWithPath("bucketName").description(
                                        "GCS 버킷 이름 (mamomo-banner-storage)"),
                                fieldWithPath("filePath").description("디렉터리 이름 (userId와 동일)"),
                                fieldWithPath("fileName").description(
                                        "파일 이름 ( {캠페인id_yyyyMMddHHss} 형식)")
                        )
                ));
    }
}