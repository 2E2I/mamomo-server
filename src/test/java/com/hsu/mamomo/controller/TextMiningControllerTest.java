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
                .text("[앵커]아이의 몸을 때리는 것만 학대가 아니죠. 아이의 마음에 상처를 남기는 것 역시 학대입니다. 최근 대법원이 이렇게 아이 마음을 다치게 하는 '정서적 학대'에 대한 처벌 수위를 높였습니다만, 수사가 제대로 이뤄지지 않는 경우가 많습니다.왜 그런지, 또 이 문제를 해결할 방법은 없는지, 박지영 기자가 짚어봤습니다.[기자]다들 무리를 지어 놀고 있는데, 한 아이만 덩그러니 구석에 앉아있습니다.교사가 오더니 발로 툭툭 차며 식판을 거칠게 내려놓습니다.지난 2월 JTBC는 경기도의 한 어린이집에서 발생한 아동학대 사건을 보도했습니다.조사 결과 일부 교사들이 아이 4명에게 신체적 학대와 함께 정서적 학대를 한 것으로 드러났습니다.교실 구석에 혼자 있게 하거나, 아이들끼리 싸움을 부추기는 식입니다.CCTV 영상 덕분에, 해당 교사는 최근 1심에서 유죄를 선고받았습니다.하지만 정서적 학대의 경우, 이렇게 물증을 확보할 수 있는 경우가 드뭅니다.정서적 학대의 상처는 보이지 않는 아이의 마음에 남기 때문입니다.이 모씨 역시 지난해 아이가 유치원에서 벌어진 일을 털어놓기 전까진 잘 몰랐습니다.[이모 씨/피해아동 어머니 : (선생님이) 그만 먹고 싶다고 해도 반찬이랑 밥을 다 섞어서 강아지밥처럼 먹이고…]아이는 피해를 당했다고 고백했지만, 유치원 측은 아이의 주장을 부인했습니다.이미 사건이 일어난지 한참 지나 CCTV를 확인하기에도 늦은 때였습니다.유일한 증거는 아이의 진술 뿐.8개월째 수사를 하고 있지만, 명확한 결론을 내리지 못했습니다.[오선희/변호사 : 눈에 띄는 학대가 아니기 때문에… (진술을 해석할 때) 아동이 처한 보육환경, 가정환경 등을 적극적으로 해석하는 문제도 결부돼 있죠.]정서적 학대에 대한 처벌 수위가 높아지긴 했지만, 현장에선 철저한 점검과 교육이 더 시급하단 지적도 나옵니다.아이에게 정서적인 상처를 주는 걸 아직 학대로 인식하지 못하는 경우도 많기 때문입니다.[공혜정/대한아동학대방지협회 대표 : (지금처럼) 동영상만 봐서는 교육의 효과가 없다… 현재로선 너무 형식적으로 하고 있지 않나…]")
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