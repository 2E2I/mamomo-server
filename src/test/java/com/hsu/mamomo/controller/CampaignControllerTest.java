package com.hsu.mamomo.controller;


import static com.hsu.mamomo.controller.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.controller.ApiDocumentUtils.getDocumentResponse;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class CampaignControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void 캠페인_기본() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("campaigns-default",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        relaxedResponseFields(
                                fieldWithPath("campaigns.[].id").description("캠페인 아이디"),
                                fieldWithPath("campaigns.[].siteType").description("기부 사이트 종류"),
                                fieldWithPath("campaigns.[].url").description("URL 주소"),
                                fieldWithPath("campaigns.[].title").description("제목"),
                                fieldWithPath("campaigns.[].tags").description("태그"),
                                fieldWithPath("campaigns.[].body").description("본문"),
                                fieldWithPath("campaigns.[].organizationName").description("조직 기관"),
                                fieldWithPath("campaigns.[].thumbnail").description("썸네일"),
                                fieldWithPath("campaigns.[].dueDate").description("마감 날짜"),
                                fieldWithPath("campaigns.[].startDate").description("시작 날짜"),
                                fieldWithPath("campaigns.[].targetPrice").description("목표 금액"),
                                fieldWithPath("campaigns.[].statusPrice").description("현재 금액"),
                                fieldWithPath("campaigns.[].percent").description("달성 정도")
                        )
                ));
    }

    @Test
    void 캠페인_최신순() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .content("{\"sort\": \"start_date,desc\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("campaigns-latest",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("sort").description("정렬 방식 - 최신 순")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("campaigns.[].id").description("캠페인 아이디"),
                                fieldWithPath("campaigns.[].siteType").description("기부 사이트 종류"),
                                fieldWithPath("campaigns.[].url").description("URL 주소"),
                                fieldWithPath("campaigns.[].title").description("제목"),
                                fieldWithPath("campaigns.[].tags").description("태그"),
                                fieldWithPath("campaigns.[].body").description("본문"),
                                fieldWithPath("campaigns.[].organizationName").description("조직 기관"),
                                fieldWithPath("campaigns.[].thumbnail").description("썸네일"),
                                fieldWithPath("campaigns.[].dueDate").description("마감 날짜"),
                                fieldWithPath("campaigns.[].startDate").description("시작 날짜"),
                                fieldWithPath("campaigns.[].targetPrice").description("목표 금액"),
                                fieldWithPath("campaigns.[].statusPrice").description("현재 금액"),
                                fieldWithPath("campaigns.[].percent").description("달성 정도")
                        )
                ));
    }

    @Test
    void 캠페인_마감순() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .content("{\"sort\": \"due_date,asc\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("campaigns-deadline",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("sort").description("정렬 방식 - 마감 순")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("campaigns.[].id").description("캠페인 아이디"),
                                fieldWithPath("campaigns.[].siteType").description("기부 사이트 종류"),
                                fieldWithPath("campaigns.[].url").description("URL 주소"),
                                fieldWithPath("campaigns.[].title").description("제목"),
                                fieldWithPath("campaigns.[].tags").description("태그"),
                                fieldWithPath("campaigns.[].body").description("본문"),
                                fieldWithPath("campaigns.[].organizationName").description("조직 기관"),
                                fieldWithPath("campaigns.[].thumbnail").description("썸네일"),
                                fieldWithPath("campaigns.[].dueDate").description("마감 날짜"),
                                fieldWithPath("campaigns.[].startDate").description("시작 날짜"),
                                fieldWithPath("campaigns.[].targetPrice").description("목표 금액"),
                                fieldWithPath("campaigns.[].statusPrice").description("현재 금액"),
                                fieldWithPath("campaigns.[].percent").description("달성 정도")
                        )
                ));
    }
}