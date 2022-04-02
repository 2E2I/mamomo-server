package com.hsu.mamomo.controller;


import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getCategoryFormat;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getSortFormat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
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
    void Campaign() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/campaigns?")
                        .param("sort", "start_date,desc")
                        .param("category", String.valueOf(8))
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
                .andExpect(jsonPath("$.campaigns.[0].['isHeart']").isBoolean())
                .andExpect(jsonPath("$.campaigns.[0].['heartCount']").isNumber())
                .andDo(document("campaigns",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("sort").description(
                                                "- 캠페인 리스트 정렬 방식\n\n"
                                                        + "default값은 최신 순\n\n"
                                                        + "- 사용 가능한 값:\n\n"
                                                        + "정렬 대상: start_date, due_date\n\n"
                                                        + "정렬 방법: asc, desc\n\n"
                                                        + "예) sort=start_date,desc (최신 순)\n\n"
                                                        + "sort=due_date,asc (마감 순)")
                                        .optional()
                                        .attributes(getSortFormat()),
                                parameterWithName("category").description(
                                                "- 캠페인 카테고리\n\n"
                                                        + "- 사용 가능한 값:\n\n"
                                                        + "1: 아동|청소년\n\n"
                                                        + "2: 어르신\n\n"
                                                        + "3: 장애인\n\n"
                                                        + "4: 어려운이웃\n\n"
                                                        + "5: 다문화\n\n"
                                                        + "6: 지구촌\n\n"
                                                        + "7: 가족|여성\n\n"
                                                        + "8: 우리사회\n\n"
                                                        + "9: 동물\n\n"
                                                        + "10: 환경")
                                        .optional()
                                        .attributes(getCategoryFormat())
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
                                        .description("달성 정도"),
                                fieldWithPath("campaigns.[].isHeart").type(JsonFieldType.BOOLEAN)
                                        .description("좋아요 여부"),
                                fieldWithPath("campaigns.[].heartCount").type(JsonFieldType.NUMBER)
                                        .description("좋아요 갯수")
                        )
                ));
    }

    @Test
    void Campaign_All() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-default",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    void Campaign_Sort() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .param("sort", "start_date,desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-sort",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    void Campaign_Category() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .param("category", String.valueOf(8))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-category",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }
}
