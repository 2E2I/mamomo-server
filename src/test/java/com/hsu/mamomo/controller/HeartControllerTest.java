package com.hsu.mamomo.controller;

import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.mamomo.config.jpa.JpaConfig;
import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.domain.Heart;
import com.hsu.mamomo.repository.elastic.CampaignRepository;
import com.hsu.mamomo.repository.jpa.HeartRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, String> input = new HashMap<>();

    @BeforeEach
    void setBody() {
        Optional<Campaign> campaign = campaignRepository.findDistinctBySiteType("happybean");
        if (campaign.isEmpty()) {
            throw new ResourceNotFoundException("캠페인을 찾을 수 없음");
        }

        input.put("campaignId", campaign.get().getId());
        input.put("userId", "550e8400-e29b-41d4-a716-446655440000"); // testUser
    }

    @Test
    @Order(100)
    @DisplayName("좋아요 테스트 - 성공")
    public void doHeart() throws Exception {

        mockMvc
                .perform(post("/api/heart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))

                .andExpect(status().isCreated())

                .andDo(document("doHeart-success",
                        getDocumentRequest(),
                        getDocumentResponse(),
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))

                .andExpect(status().isOk())

                .andDo(document("unHeart-success",
                        getDocumentRequest(),
                        getDocumentResponse(),
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
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("HEART_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("해당 좋아요 정보를 찾을 수 없습니다."));


    }

}
