package com.hsu.mamomo.controller;



import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getSortFormat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
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
                .andExpect(jsonPath("$.campaigns").isArray())
                .andExpect(jsonPath("$.campaigns.[0].['id']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['siteType']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['url']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['title']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['tags']").isArray())
                .andExpect(jsonPath("$.campaigns.[0].['body']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['organizationName']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['thumbnail']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['dueDate']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['startDate']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['targetPrice']").isNumber())
                .andExpect(jsonPath("$.campaigns.[0].['statusPrice']").isNumber())
                .andExpect(jsonPath("$.campaigns.[0].['percent']").isNumber())
                .andDo(document("campaigns-default",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        relaxedResponseFields(
                                fieldWithPath("campaigns").type(JsonFieldType.ARRAY)
                                        .description("캠페인 리스트"),
                                fieldWithPath("campaigns.[].id").type(JsonFieldType.STRING)
                                        .description("캠페인 아이디"),
                                fieldWithPath("campaigns.[].siteType").type(JsonFieldType.STRING)
                                        .description("사이트 타입"),
                                fieldWithPath("campaigns.[].url").type(JsonFieldType.STRING)
                                        .description("URL 주소"),
                                fieldWithPath("campaigns.[].title").type(JsonFieldType.STRING)
                                        .description("제목"),
                                fieldWithPath("campaigns.[].tags").type(JsonFieldType.ARRAY)
                                        .description("태그 모음"),
                                fieldWithPath("campaigns.[].body").type(JsonFieldType.STRING)
                                        .description("본문"),
                                fieldWithPath("campaigns.[].organizationName").type(
                                        JsonFieldType.STRING).description("조직 기관"),
                                fieldWithPath("campaigns.[].thumbnail").type(JsonFieldType.STRING)
                                        .description("썸네일"),
                                fieldWithPath("campaigns.[].dueDate").type(JsonFieldType.STRING)
                                        .description("마감 날짜"),
                                fieldWithPath("campaigns.[].startDate").type(JsonFieldType.STRING)
                                        .description("시작 날짜"),
                                fieldWithPath("campaigns.[].targetPrice").type(JsonFieldType.NUMBER)
                                        .description("목표 금액"),
                                fieldWithPath("campaigns.[].statusPrice").type(JsonFieldType.NUMBER)
                                        .description("현재 금액"),
                                fieldWithPath("campaigns.[].percent").type(JsonFieldType.NUMBER)
                                        .description("달성 정도")
                        )
                ));
    }

    @Test
    void 캠페인_최신순() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .content("{\"sort\": \"start_date,desc\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaigns").isArray())
                .andExpect(jsonPath("$.campaigns.[0].['id']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['siteType']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['url']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['title']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['tags']").isArray())
                .andExpect(jsonPath("$.campaigns.[0].['body']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['organizationName']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['thumbnail']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['dueDate']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['startDate']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['targetPrice']").isNumber())
                .andExpect(jsonPath("$.campaigns.[0].['statusPrice']").isNumber())
                .andExpect(jsonPath("$.campaigns.[0].['percent']").isNumber())
                .andDo(document("campaigns-latest",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("sort").description(
                                                "캠페인 리스트 정렬 방식을 결정하는 파라미터, "
                                                        + "정렬 대상은 'start_date','due_date' 중 하나,"
                                                        + "정렬 방법은 'asc', 'desc' 중 하나")
                                        .optional()
                                        .attributes(getSortFormat())
                        ),
                        relaxedResponseFields(
                                fieldWithPath("campaigns").type(JsonFieldType.ARRAY)
                                        .description("캠페인 리스트"),
                                fieldWithPath("campaigns.[].id").type(JsonFieldType.STRING)
                                        .description("캠페인 아이디"),
                                fieldWithPath("campaigns.[].siteType").type(JsonFieldType.STRING)
                                        .description("사이트 타입"),
                                fieldWithPath("campaigns.[].url").type(JsonFieldType.STRING)
                                        .description("URL 주소"),
                                fieldWithPath("campaigns.[].title").type(JsonFieldType.STRING)
                                        .description("제목"),
                                fieldWithPath("campaigns.[].tags").type(JsonFieldType.ARRAY)
                                        .description("태그 모음"),
                                fieldWithPath("campaigns.[].body").type(JsonFieldType.STRING)
                                        .description("본문"),
                                fieldWithPath("campaigns.[].organizationName").type(
                                        JsonFieldType.STRING).description("조직 기관"),
                                fieldWithPath("campaigns.[].thumbnail").type(JsonFieldType.STRING)
                                        .description("썸네일"),
                                fieldWithPath("campaigns.[].dueDate").type(JsonFieldType.STRING)
                                        .description("마감 날짜"),
                                fieldWithPath("campaigns.[].startDate").type(JsonFieldType.STRING)
                                        .description("시작 날짜"),
                                fieldWithPath("campaigns.[].targetPrice").type(JsonFieldType.NUMBER)
                                        .description("목표 금액"),
                                fieldWithPath("campaigns.[].statusPrice").type(JsonFieldType.NUMBER)
                                        .description("현재 금액"),
                                fieldWithPath("campaigns.[].percent").type(JsonFieldType.NUMBER)
                                        .description("달성 정도")
                        )
                ));
    }

    @Test
    void 캠페인_마감순() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .content("{\"sort\": \"due_date,asc\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaigns").isArray())
                .andExpect(jsonPath("$.campaigns.[0].['id']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['siteType']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['url']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['title']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['tags']").isArray())
                .andExpect(jsonPath("$.campaigns.[0].['body']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['organizationName']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['thumbnail']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['dueDate']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['startDate']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['targetPrice']").isNumber())
                .andExpect(jsonPath("$.campaigns.[0].['statusPrice']").isNumber())
                .andExpect(jsonPath("$.campaigns.[0].['percent']").isNumber())
                .andDo(document("campaigns-deadline",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("sort").description(
                                                "캠페인 리스트 정렬 방식을 결정하는 파라미터, "
                                                        + "정렬 대상은 'start_date','due_date' 중 하나,"
                                                        + "정렬 방법은 'asc', 'desc' 중 하나")
                                        .optional()
                                        .attributes(getSortFormat())
                        ),
                        relaxedResponseFields(
                                fieldWithPath("campaigns").type(JsonFieldType.ARRAY)
                                        .description("캠페인 리스트"),
                                fieldWithPath("campaigns.[].id").type(JsonFieldType.STRING)
                                        .description("캠페인 아이디"),
                                fieldWithPath("campaigns.[].siteType").type(JsonFieldType.STRING)
                                        .description("사이트 타입"),
                                fieldWithPath("campaigns.[].url").type(JsonFieldType.STRING)
                                        .description("URL 주소"),
                                fieldWithPath("campaigns.[].title").type(JsonFieldType.STRING)
                                        .description("제목"),
                                fieldWithPath("campaigns.[].tags").type(JsonFieldType.ARRAY)
                                        .description("태그 모음"),
                                fieldWithPath("campaigns.[].body").type(JsonFieldType.STRING)
                                        .description("본문"),
                                fieldWithPath("campaigns.[].organizationName").type(
                                        JsonFieldType.STRING).description("조직 기관"),
                                fieldWithPath("campaigns.[].thumbnail").type(JsonFieldType.STRING)
                                        .description("썸네일"),
                                fieldWithPath("campaigns.[].dueDate").type(JsonFieldType.STRING)
                                        .description("마감 날짜"),
                                fieldWithPath("campaigns.[].startDate").type(JsonFieldType.STRING)
                                        .description("시작 날짜"),
                                fieldWithPath("campaigns.[].targetPrice").type(JsonFieldType.NUMBER)
                                        .description("목표 금액"),
                                fieldWithPath("campaigns.[].statusPrice").type(JsonFieldType.NUMBER)
                                        .description("현재 금액"),
                                fieldWithPath("campaigns.[].percent").type(JsonFieldType.NUMBER)
                                        .description("달성 정도")
                        )
                ));
    }
}