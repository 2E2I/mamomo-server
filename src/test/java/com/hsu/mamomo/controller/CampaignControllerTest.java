package com.hsu.mamomo.controller;


import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getCategoryFormat;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getSortFormat;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.hsu.mamomo.util.CampaignDocumentUtil;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class CampaignControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void Campaign() throws Exception {

        ResultActions resultActions = mockMvc
                .perform(RestDocumentationRequestBuilders.get("/api/campaigns?")
                        .param("sort", "start_date,desc")
                        .param("category", String.valueOf(8))
                        .contentType(MediaType.APPLICATION_JSON))

                // document
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
                                CampaignDocumentUtil.getCampaginParameterWithName_Category()
                                        .optional()
                                        .attributes(getCategoryFormat())
                        ),
                        CampaignDocumentUtil.getCampaignResponseFields()
                ));

        // then
        resultActions = CampaignDocumentUtil.getCampaignExpect(resultActions);
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
