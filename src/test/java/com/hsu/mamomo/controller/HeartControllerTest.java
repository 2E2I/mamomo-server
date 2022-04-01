package com.hsu.mamomo.controller;

import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.mamomo.config.jpa.JpaConfig;
import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.repository.elastic.CampaignRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
        Optional<Campaign> campaign = campaignRepository.findAll().stream().findFirst();
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

}
