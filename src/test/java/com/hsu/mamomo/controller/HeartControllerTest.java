package com.hsu.mamomo.controller;

import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hsu.mamomo.config.jpa.JpaConfig;
import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.repository.elastic.CampaignRepository;
import com.hsu.mamomo.repository.jpa.HeartRepository;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.elasticsearch.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(JpaConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HeartControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CampaignRepository campaignRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, String> input = new HashMap<>();
    private String jwtToken;

    @BeforeEach
    void setBody() throws Exception {
//        Optional<Campaign> campaign = campaignRepository.findFirstBySiteTypeIs("kakao");
//        if (campaign.isEmpty()) {
//            throw new ResourceNotFoundException("캠페인을 찾을 수 없음");
//        }

        input.put("campaignId", "H000000184164");
        input.put("userId", "550e8400-e29b-41d4-a716-446655440000"); // testUser

        Map<String, String> auth = new HashMap<>();
        auth.put("email", "test@email.com");
        auth.put("password", "testPassword");

        MvcResult mvcResult = mockMvc
                .perform(RestDocumentationRequestBuilders.post("/api/user/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(auth)))
                .andExpect(status().isOk())
                .andReturn();

        // 응답 바디의 jwt 토큰 추출
        String responseBody = mvcResult.getResponse().getContentAsString();
        TokenDto tokenDto = objectMapper.registerModule(new JavaTimeModule())
                .readValue(responseBody, TokenDto.class);
        jwtToken = tokenDto.getToken();

        // 발급된 토큰이 유효한 jwt 토큰인지 확인
        assertTrue(jwtTokenProvider.validateToken(tokenDto.getToken()));
    }

    @Test
    @Order(100)
    @DisplayName("좋아요 테스트 - 성공")
    public void doHeart() throws Exception {

        mockMvc
                .perform(post("/api/heart")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))

                .andExpect(status().isCreated())

                .andDo(document("doHeart-success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("발급받은 jwt 토큰.\n\n"
                                                + "토큰 앞에 'Bearer '을 붙인다.")
                        ),
                        requestFields(
                                fieldWithPath("campaignId").description("좋아요 할 캠페인 ID"),
                                fieldWithPath("userId").description("좋아요 누르는 유저 ID")
                        )));
    }

    @Test
    @Order(101)
    @DisplayName("좋아요 테스트 - 실패 :: 이미 좋아요 된 캠페인")
    public void doHeartFailDuplicate() throws Exception {

        mockMvc
                .perform(post("/api/heart")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))

                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ALREADY_HEARTED"))
                .andExpect(jsonPath("$.message").value("이미 좋아요 된 캠페인 입니다."));
    }

    @Test
    @Order(200)
    @DisplayName("좋아요 취소 테스트 - 성공")
    public void unHeart() throws Exception {

        mockMvc
                .perform(delete("/api/heart")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))

                .andExpect(status().isOk())

                .andDo(document("unHeart-success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("발급받은 jwt 토큰.\n\n"
                                                + "토큰 앞에 'Bearer '을 붙인다.")
                        ),
                        requestFields(
                                fieldWithPath("campaignId").description("좋아요 취소 할 캠페인 ID"),
                                fieldWithPath("userId").description("좋아요 취소 누르는 유저 ID")
                        )));

    }

    @Test
    @Order(201)
    @DisplayName("좋아요 취소 테스트 - 실패 :: 없는 좋아요 취소 시도")
    public void unHeartFailNotFound() throws Exception {

        mockMvc
                .perform(delete("/api/heart")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("HEART_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("해당 좋아요 정보를 찾을 수 없습니다."));


    }

}
