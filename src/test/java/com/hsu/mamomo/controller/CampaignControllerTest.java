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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
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

        // 응답 바디의 jwt 토큰 추출
        String responseBody = mvcResult.getResponse().getContentAsString();
        TokenDto tokenDto = objectMapper.readValue(responseBody, TokenDto.class);
        jwtToken = tokenDto.getToken();
        System.out.println("jwtToken = " + jwtToken);

        // 발급된 토큰이 유효한 jwt 토큰인지 확인
        assertTrue(jwtTokenProvider.validateToken(tokenDto.getToken()));
    }

    @Test
    @DisplayName("캠페인 조회 테스트 - 성공 :: 모든 파라미터 O/페이지,사이즈,정렬,카테고리,인증헤더")
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
                                        .description("발급받은 jwt 토큰.\n\n"
                                                + "토큰 앞에 'Bearer '을 붙인다."
                                                + "인증 되었을 경우 유저에 따른 isHeart 여부가 표시된다.")
                        ),
                        pathParameters(
                                CampaignDocumentUtil.getCampaignParameterWithName_Size(),
                                CampaignDocumentUtil.getCampaignParameterWithName_Page(),
                                parameterWithName("sort").description(
                                        "- 캠페인 리스트 정렬 방식\n\n"
                                                + "default 값은 최신 순\n\n"
                                                + "- 사용 가능한 값:\n\n"
                                                + "정렬 대상: start_date, due_date ...\n\n"
                                                + "정렬 방향: ASC,DESC\n\n"
                                                + "예) sort=start_date,DESC (최신 순)\n\n"
                                                + "sort=due_date,ASC (마감 순)")
                                        .optional()
                                        .attributes(getSortFormat()),
                                CampaignDocumentUtil.getCampaginParameterWithName_Category()
                                        .optional()
                                        .attributes(getCategoryFormat()),
                                parameterWithName("keyword").description("검색 키워드").optional()
                        ),
                        CampaignDocumentUtil.getCampaignResponseFields()
                ));

        // then
        resultActions = CampaignDocumentUtil.getCampaignExpect(resultActions);
    }

    @Test
    @DisplayName("캠페인 조회 테스트 - 성공 :: 아무 옵션 없이")
    void Campaign_All() throws Exception {
        mockMvc.perform(get("/api/campaigns")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-default",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("캠페인 조회 테스트 - 성공 :: 정렬 추가")
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
    @DisplayName("캠페인 조회 테스트 - 성공 :: 카테고리 추가")
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
    @DisplayName("캠페인 검색 테스트 - 성공 :: 키워드로 검색, 정렬 X, 정확도순")
    public void Campaign_Search() throws Exception {

        mockMvc.perform(get("/api/campaigns")
                .param("keyword", "노인")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(document("campaigns-search-with-keyword-no-sort",
                        getDocumentRequest(),
                        getDocumentResponse()
                ));
    }

    @Test
    @DisplayName("캠페인 검색 테스트 - 성공 :: 키워드로 검색, 정렬 지정")
    public void searchCampaignsByKeyword() throws Exception {

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/campaigns?")
                .param("sort", "start_date,DESC")
                .param("keyword", "노인")
                .contentType(MediaType.APPLICATION_JSON))

                .andDo(document("campaigns-search-with-keyword-sort",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                CampaignDocumentUtil.getCampaignParameterWithName_Size(),
                                CampaignDocumentUtil.getCampaignParameterWithName_Page(),
                                parameterWithName("sort").description(
                                        "- 캠페인 리스트 정렬 방식\n\n"
                                                + "정렬 하지 않으려면 sort=none,none이나 sort=none\n\n"
                                                + "- 사용 가능한 값:\n\n"
                                                + "정렬 대상: start_date, due_date ...\n\n"
                                                + "정렬 방향: ASC,DESC\n\n"
                                                + "예) sort=start_date,DESC (최신 순)\n\n"
                                                + "sort=due_date,ASC (마감 순)")
                                        .optional()
                                        .attributes(getSortFormat()),
                                parameterWithName("keyword").description("검색 키워드").optional()
                        )
                ));
    }

}
