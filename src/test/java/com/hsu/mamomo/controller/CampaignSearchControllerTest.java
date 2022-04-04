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
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hsu.mamomo.util.CampaignDocumentUtil;
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
import org.springframework.test.web.servlet.ResultActions;

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
                .andExpect(jsonPath("$.top10Tags").isArray())
                .andExpect(jsonPath("$.top10Tags", hasSize(10)))
                .andDo(document("return-top-10-tags",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("top10Tags").description("상위 10개 태그 반환")
                        )));
    }

    @DisplayName("검색 키워드로 캠페인 글 검색")
    @Test
    public void searchCampaignsByKeyword() throws Exception {

        ResultActions resultActions = mockMvc.perform(get("/api/search/campaigns?")
                .param("keyword", "노인")
                .contentType(MediaType.APPLICATION_JSON))

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
                CampaignDocumentUtil.getCampaignResponseFields()
        ));

        // then
        resultActions = CampaignDocumentUtil.getCampaignExpect(resultActions);
    }
}