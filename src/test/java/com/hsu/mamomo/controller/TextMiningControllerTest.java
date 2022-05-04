package com.hsu.mamomo.controller;

import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.mamomo.dto.TextDto;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class TextMiningControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static private TextDto textDto;

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

        // 발급된 토큰이 유효한 jwt 토큰인지 확인
        assertTrue(jwtTokenProvider.validateToken(tokenDto.getToken()));
    }

    @BeforeEach
    public void setTextDto() {
        textDto = TextDto.builder()
                .text("더행복나눔사회적협동조합 서정행복한홈스쿨 지역아동센터(아동복지시설)는 평택시 지역사회 내 결손가정 및 저소득 가정의 아동과 청소년 대상으로 다양한 서비스(보호-교육-문화-복지)를 제공하고 있습니다. 본 기관이 위치해 있는 서정동은 교육복지우선지원사업 지역으로서 저소득 가구가 밀집되어 있습니다.  저희 기관 이용 아동들의 가정은 대부분 주 양육자의 경제활동으로 인한 이른 출근과 늦은 퇴근으로 돌봄의 부재가 발생하고 있어 돌봄이 필요한 아동들을 유해환경으로부터 보호하고 아동과 가정 상황을 고려한 맞춤형 서비스를 제공하고 있습니다.  서정행복한홈스쿨 지역아동센터 이용아동들의 사회적 및 정서적 발달을 도모함으로써 지역사회 내 건강한 사회 구성원으로 성장할 수 있도록 돕는 통합적인 복지 서비스를 제공하고자 최선을 다하겠습니다.코로나 19 장기화로 인해 일상생활 속에서 받는 어려움과 스트레스를 해소할 수 있는 기회가 많이 줄어들었습니다. 현재 서정행복한홈스쿨 지역아동센터가 위치한 평택시 지역사회에는 코로나19 확진자가 급증하여 아동들이 대부분의 시간을 가정과 센터에서 보내고 있습니다.  이제는 가정을 제외한 야외 공간에서 마스크를 착용하는 것이 너무 일상화되었지만, 일상생활이 어려운 빈곤 가정의 아동들에게는 매일 필요한 마스크와 모든 학용품을 준비하기에 가격이 부담되고 있는 상황입니다.  따라서 본 기관에서는 곧 다가오는 새 학기 학교생활 적응기간 동안 아동들이 안전하게 착용할 수 있는 KF94 마스크를 넉넉하게 제공하고, 학교 수업시간에 필요한 학용품(연필, 지우개) 구비를 위한 걱정보다 설레는 마음으로 새로운 출발을 할 수 있도록 새 학기 물품을 지원하고자 장바구니 종류(학용품, 가방, 책 등)로 선정하게 되었습니다.")
                .build();
    }

    @DisplayName("텍스트 마이닝 테스트 - 성공 :: 텍스트 마이닝으로 캠페인 추천")
    @Test
    public void getCampaignsByTextMining() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post("/api/textMining")
                        .param("page", String.valueOf(0))
                        .param("size", String.valueOf(20))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .content(objectMapper.writeValueAsString(textDto))
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())

                // document
                .andDo(document("text-mining",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        // 요청 필드 문서화
                        requestFields(
                                fieldWithPath("text").description("텍스트 마이닝 요청할 텍스트")
                        ),
                        pathParameters(
                                CampaignDocumentUtil.getCampaignParameterWithName_Size(),
                                CampaignDocumentUtil.getCampaignParameterWithName_Page()
                        ),
                        CampaignDocumentUtil.getCampaignResponseFields()
                ));
    }
}