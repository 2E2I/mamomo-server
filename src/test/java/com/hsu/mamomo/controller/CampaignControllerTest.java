package com.hsu.mamomo.controller;


import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getCategoryFormat;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getSortFormat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.repository.elastic.CampaignRepository;
import com.hsu.mamomo.util.CampaignDocumentUtil;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class CampaignControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static private String jwtToken;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CampaignRepository campaignRepository;

    @BeforeEach
    void authenticate() throws Exception {

        Map<String, String> input = new HashMap<>();
        input.put("email", "test@email.com");
        input.put("password", "testPassword");

        MvcResult mvcResult = mockMvc
                .perform(RestDocumentationRequestBuilders.post("/api/user/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn();

        // ?????? ????????? jwt ?????? ??????
        String responseBody = mvcResult.getResponse().getContentAsString();
        TokenDto tokenDto = objectMapper.readValue(responseBody, TokenDto.class);
        jwtToken = tokenDto.getToken();

        // ????????? ????????? ????????? jwt ???????????? ??????
        assertTrue(jwtTokenProvider.validateToken(tokenDto.getToken()));
    }

    @Test
    @DisplayName("????????? ?????? ????????? - ?????? :: ?????? ???????????? O/?????????,?????????,??????,????????????,????????????")
    void Campaign() throws Exception {

        ResultActions resultActions = mockMvc
                .perform(RestDocumentationRequestBuilders.get("/api/campaigns?")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(3))
                        .param("sort", "due_date,DESC")
                        .param("category", String.valueOf(1))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))

                // document
                .andDo(document("campaigns",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("???????????? jwt ??????.\n\n"
                                                             + "?????? ?????? 'Bearer '??? ?????????."
                                                             + "?????? ????????? ?????? ????????? ?????? isHeart ????????? ????????????.")
                        ),
                        pathParameters(
                                CampaignDocumentUtil.getCampaignParameterWithName_Size(),
                                CampaignDocumentUtil.getCampaignParameterWithName_Page(),
                                parameterWithName("sort").description(
                                                "- ????????? ????????? ?????? ??????\n\n"
                                                        + "default ?????? ?????? ???\n\n"
                                                        + "- ?????? ????????? ???:\n\n"
                                                        + "?????? ??????: start_date, due_date, heart_count ...\n\n"
                                                        + "?????? ??????: ASC,DESC\n\n"
                                                        + "???) sort=start_date,DESC (?????? ???)\n\n"
                                                        + "sort=due_date,ASC (?????? ???)")
                                        .optional()
                                        .attributes(getSortFormat()),
                                CampaignDocumentUtil.getCampaginParameterWithName_Category()
                                        .optional()
                                        .attributes(getCategoryFormat()),
                                parameterWithName("keyword").description("?????? ?????????").optional()
                        ),
                        CampaignDocumentUtil.getCampaignResponseFields()
                ));

        // then
        resultActions = CampaignDocumentUtil.getCampaignExpect(resultActions);
    }

    @Test
    @DisplayName("????????? ?????? ????????? - ?????? :: ?????? ?????? ??????")
    void Campaign_All() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-default",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("????????? ?????? ????????? - ?????? :: ?????? ??????")
    void Campaign_Sort() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .param("sort", "start_date,DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-sort",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("????????? ?????? ????????? - ?????? :: ????????????")
    void Campaign_sortHeartCount() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .param("sort", "heart_count,DESC")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-sort-heart",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("????????? ?????? ????????? - ?????? :: ???????????? ??????")
    void Campaign_Category() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                        .param("category", String.valueOf(8))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-category",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("????????? ?????? ????????? - ?????? :: ???????????? ??????, ?????? X, ????????????")
    public void Campaign_Search() throws Exception {

        mockMvc.perform(get("/api/campaigns")
                        .param("keyword", "??????")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-search-with-keyword-no-sort",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("????????? ?????? ????????? - ?????? :: ???????????? ??????, ?????? ??????")
    public void searchCampaignsByKeyword() throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/campaigns?")
                        .param("sort", "start_date,DESC")
                        .param("keyword", "??????")
                        .contentType(MediaType.APPLICATION_JSON))

                .andDo(document("campaigns-search-with-keyword-sort",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                CampaignDocumentUtil.getCampaignParameterWithName_Size(),
                                CampaignDocumentUtil.getCampaignParameterWithName_Page(),
                                parameterWithName("sort").description(
                                                "- ????????? ????????? ?????? ??????\n\n"
                                                        + "?????? ?????? ???????????? sort=none,none?????? sort=none\n\n"
                                                        + "- ?????? ????????? ???:\n\n"
                                                        + "?????? ??????: start_date, due_date ...\n\n"
                                                        + "?????? ??????: ASC,DESC\n\n"
                                                        + "???) sort=start_date,DESC (?????? ???)\n\n"
                                                        + "sort=due_date,ASC (?????? ???)")
                                        .optional()
                                        .attributes(getSortFormat()),
                                parameterWithName("keyword").description("?????? ?????????").optional()
                        )
                ));
    }

    @Test
    @DisplayName("????????? ????????? - ?????? :: ????????? id??? ????????? ?????? ??????")
    public void findCampaignByIdTest() throws Exception {

        String campaignId = campaignRepository.findFirstBySiteTypeIs("kakao").get().getId();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/campaign/{id}", campaignId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("get-campaign-by-id",
                        getDocumentRequest(),
                        getDocumentResponse()
                ))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName(("????????? ????????? - ?????? :: ????????? ?????? ?????????"))
    public void findCampaignsByHeartList() throws Exception {
        mockMvc.perform(get("/api/campaigns/heartList")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-heart-list",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("????????? ????????? - ?????? :: ????????? ?????? ??? ??????")
    public void findCampaignByIdFailNotFoundTest() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.get("/api/campaign/{id}", "this is not valid id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("get-campaign-by-id",
                        getDocumentRequest(),
                        getDocumentResponse()
                )).andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CAMPAIGN_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("?????? ????????? ????????? ?????? ??? ????????????."));
    }

}
