package com.hsu.mamomo.controller;


import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getSortFormat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
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
class CampaignSearchControllerTest {

    @Autowired
    MockMvc mockMvc;

    @DisplayName("상위 10개 태그 반환")
    @Test
    public void returnTop10Tags() throws Exception {
        mockMvc.perform(get("/api/search").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.top_10_tags").isArray())
                .andExpect(jsonPath("$.top_10_tags", hasSize(10)))
                .andDo(document("return-top-10-tags",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("top_10_tags").description("상위 10개 태그 반환")
                        )));
    }

    @DisplayName("검색 키워드로 캠페인 글 검색")
    @Test
    public void searchCampaignsByKeyword() throws Exception {
        mockMvc.perform(get("/api/search/campaigns?")
                .param("keyword", "노인")
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaigns").isArray())
                .andExpect(jsonPath("$.campaigns.[0].['id']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['siteType']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['url']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['title']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['category']").isArray())
                .andExpect(jsonPath("$.campaigns.[0].['tags']").isArray())
                .andExpect(jsonPath("$.campaigns.[0].['body']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['organizationName']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['thumbnail']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['dueDate']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['startDate']").isString())
                .andExpect(jsonPath("$.campaigns.[0].['targetPrice']").isNumber())
                .andExpect(jsonPath("$.campaigns.[0].['statusPrice']").isNumber())
                .andExpect(jsonPath("$.campaigns.[0].['percent']").isNumber())

                .andDo(document("search-campaigns-by-keyword",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("keyword").description(
                                        "2글자 이상 검색어 입력"
                                ),
                                parameterWithName("sort").description(
                                        "- 캠페인 리스트 정렬 방식\n\n"
                                                + "default 값은 정확도순\n\n"
                                                + "- 사용 가능한 값:\n\n"
                                                + "정렬 대상: start_date, due_date\n\n"
                                                + "정렬 방법: asc, desc\n\n"
                                                + "예) sort=start_date,desc (최신 순)\n\n"
                                                + "sort=due_date,asc (마감 순)")
                                        .optional()
                                        .attributes(getSortFormat())
                        ),
                        responseFields(
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
                                fieldWithPath("campaigns.[].category").type(JsonFieldType.ARRAY)
                                        .description("카테고리"),
                                fieldWithPath("campaigns.[].tags").type(JsonFieldType.ARRAY)
                                        .description("태그 모음"),
                                fieldWithPath("campaigns.[].body").type(JsonFieldType.STRING)
                                        .description("본문"),
                                fieldWithPath("campaigns.[].organizationName").type(
                                        JsonFieldType.STRING).description("기부 단체"),
                                fieldWithPath("campaigns.[].thumbnail").type(JsonFieldType.STRING)
                                        .description("썸네일 URL"),
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
                        )));
    }
}