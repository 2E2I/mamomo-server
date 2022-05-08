package com.hsu.mamomo.controller;

import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.dto.banner.BannerDto;
import com.hsu.mamomo.dto.banner.BannerSaveDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BannerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static private String jwtToken;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private BannerSaveDto bannerSaveDto;
    private String userId;
    private String bannerId;

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
        bannerSaveDto = BannerSaveDto.builder()
                .email("test@gmail.com")
                .campaignId("824")
                .bannerImg(new MockMultipartFile("bannerImg",
                        "test.jpg",
                        "image/jpeg",
                        new FileInputStream("src/test/resources/bannerTest.jpg")))
                .build();
        userId = "dbb485f4-f588-4bfe-b485-f4f5885bfe9d";
    }

    @Test
    @Order(100)
    @DisplayName("배너 저장 테스트 - 성공 :: ")
    public void saveBanner() throws Exception {
        MvcResult mvcResult = mockMvc.perform(multipart("/api/banner")
                        .file((MockMultipartFile) bannerSaveDto.getBannerImg())
                        .param("email", bannerSaveDto.getEmail())
                        .param("campaignId", bannerSaveDto.getCampaignId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))

                .andExpect(status().isOk())

                // document
                .andDo(document("banner-save",
                        getDocumentRequest(),
                        getDocumentResponse(),

                        // 요청 필드 문서화
                        requestParts(
                                partWithName("bannerImg").description("업로드 할 배너 이미지 파일")
                        ),
                        requestParameters(
                                parameterWithName("email").description("사용자 이메일"),
                                parameterWithName("campaignId").description("배너 이미지를 만들 캠페인 id")
                        ),
                        // 응답 필드 문서화
                        responseFields(
                                fieldWithPath("bannerId").description("배너 아이디"),
                                fieldWithPath("imgUrl").description("배너 이미지 주소")
                        )
                ))
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        BannerDto responseBanner = objectMapper.readValue(responseBody, BannerDto.class);
        bannerId = responseBanner.getBannerId();
        System.out.println(bannerId);

    }

    @Test
    @Order(200)
    @DisplayName("배너 리스트 테스트 - 성공 :: ")
    public void getBannerList() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/banner/{email}",
                                bannerSaveDto.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())

                // 문서화
                .andDo(document("banner-list",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("email").description("사용자 이메일")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("bannerList.[]").description("배너 리스트"),
                                fieldWithPath("bannerList.[].bannerId").description("배너 아이디"),
                                fieldWithPath("bannerList.[].imgUrl").description(
                                        "base64 인코딩된 배너 이미지 url")
                        )
                ));
    }

    @Test
    @Order(300)
    @DisplayName("배너 삭제 테스트 - 성공 :: ")
    public void deleteBanner() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/banner/{email}/{bannerId}",
                                bannerSaveDto.getEmail(), bannerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())

                // 문서화
                .andDo(document("banner-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("email").description("사용자 이메일"),
                                parameterWithName("bannerId").description("삭제할 배너 id")
                        )))
                .andDo(print()
                );
    }
}