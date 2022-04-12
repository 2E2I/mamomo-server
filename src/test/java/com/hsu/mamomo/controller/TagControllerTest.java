package com.hsu.mamomo.controller;


import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class TagControllerTest {

    @Autowired
    MockMvc mockMvc;

    @DisplayName("태그 테스트 - 성공 :: 인기태그 from 부터 to 까지 반환")
    @Test
    public void returnRangeTags() throws Exception {
        mockMvc.perform(get("/api/search")
                .param("from", String.valueOf(0))
                .param("to", String.valueOf(10))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags", hasSize(10)))
                .andDo(document("return-tags-by-range",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParameters(
                                parameterWithName("from").optional()
                                        .description("시작 인덱스. 기본값은 0"),
                                parameterWithName("to").optional()
                                        .description("종료? 인덱스. 기본값은 1000")
                        ),
                        responseFields(
                                fieldWithPath("tags").description("from 부터 to 까지 인기 태그 반환")
                        )));
    }

    @Test
    @DisplayName("태그 테스트 - 성공 :: 태그별 캠페인 조회")
    void Campaign_Category() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/tag")
                .param("tagName", "아동")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-tags",
                        getDocumentRequest(),
                        getDocumentResponse()
                )).andDo(print());
    }

}