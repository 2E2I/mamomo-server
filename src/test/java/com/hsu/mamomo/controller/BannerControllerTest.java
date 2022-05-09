package com.hsu.mamomo.controller;

import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getSortFormat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.dto.banner.BannerDto;
import com.hsu.mamomo.dto.banner.BannerSaveDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.util.CampaignDocumentUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BannerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static private String jwtToken;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private BannerSaveDto bannerSaveDto;
    private String bannerId;
    private String userId;
    private String localDateTime;

    @BeforeEach
    void authenticate() throws Exception {

        Map<String, String> input = new HashMap<>();
        input.put("email", "test@gmail.com");
        input.put("password", "password");

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
    public void setBannerDto() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        localDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        bannerSaveDto = BannerSaveDto.builder()
                .email("test@gmail.com")
                .bannerImg(new MockMultipartFile("bannerImg",
                        "test.jpg",
                        "image/jpeg",
                        new FileInputStream("src/test/resources/bannerTest.jpg")))
                .date(now)
                .build();
        userId = "dbb485f4-f588-4bfe-b485-f4f5885bfe9d";
    }

    @Test
    @Order(100)
    @DisplayName("배너 저장 테스트 - 성공 :: ")
    public String saveBanner() throws Exception {
        MvcResult mvcResult = mockMvc.perform(multipart("/api/banner")
                        .file((MockMultipartFile) bannerSaveDto.getBannerImg())
                        .param("email", bannerSaveDto.getEmail())
                        .param("date", localDateTime)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))

                .andExpect(status().isOk())

                // document
                .andDo(document("banner-save",
                        getDocumentRequest(),
                        getDocumentResponse(),

                        // 요청 필드 문서화
                        requestParts(
                                partWithName("bannerImg").description("업로드 할 배너 이미지 파일")
                        ),
                        requestParameters(
                                parameterWithName("email").description("사용자 이메일"),
                                parameterWithName("date").description("배너 만든/수정한 시간")
                        ),
                        // 응답 필드 문서화
                        relaxedResponseFields(
                                fieldWithPath("banner.bannerId").description("배너 아이디"),
                                fieldWithPath("banner.imgUrl").description("배너 이미지 주소"),
                                fieldWithPath("banner.date").description("배너 만든/수정한 시간")
                        )
                ))
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        BannerDto responseBanner = objectMapper.readValue(responseBody, BannerDto.class);
        bannerId = responseBanner.getBanner().getBannerId();
        System.out.println(bannerId);
        return bannerId;
    }

    @Test
    @Order(200)
    @DisplayName("유저 배너 리스트 테스트 - 성공 :: ")
    public void getBannerListByUser() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/banner/{email}",
                                bannerSaveDto.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())

                // 문서화
                .andDo(document("banner-list-by-user",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("email").description("사용자 이메일"),
                                CampaignDocumentUtil.getCampaignParameterWithName_Size(),
                                CampaignDocumentUtil.getCampaignParameterWithName_Page(),
                                parameterWithName("sort").description(
                                                "- 배너 리스트 정렬 방식\n\n"
                                                        + "default 값은 최신 순\n\n"
                                                        + "- 사용 가능한 값:\n\n"
                                                        + "정렬 대상: date"
                                                        + "정렬 방향: ASC,DESC\n\n"
                                                        + "예) sort=date,DESC (최신 순)\n\n"
                                                        + "sort=date,ASC (마감 순)")
                                        .optional()
                                        .attributes(getSortFormat())
                        ),
                        relaxedResponseFields(
                                fieldWithPath("bannerList.content").description("배너 리스트"),
                                fieldWithPath("bannerList.content.[].bannerId").description(
                                        "배너 아이디"),
                                fieldWithPath("bannerList.content.[].imgUrl").description(
                                        "base64 인코딩된 배너 이미지 url"),
                                fieldWithPath("bannerList.pageable")
                                        .type(JsonFieldType.OBJECT).description("페이지 정보"),
                                subsectionWithPath("bannerList.pageable.sort")
                                        .type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("bannerList.pageable.offset")
                                        .type(JsonFieldType.NUMBER).description("현재 offset"),
                                fieldWithPath("bannerList.pageable.pageNumber")
                                        .type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("bannerList.pageable.pageSize")
                                        .type(JsonFieldType.NUMBER).description("페이지 하나의 크기"),
                                fieldWithPath("bannerList.pageable.paged")
                                        .type(JsonFieldType.BOOLEAN).description("페이지 처리가 되었는지 여부"),
                                fieldWithPath("bannerList.pageable.unpaged")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("페이지 처리가 안되었는지 여부"),
                                fieldWithPath("bannerList.last")
                                        .type(JsonFieldType.BOOLEAN).description("마지막 페이지 인지 여부"),
                                fieldWithPath("bannerList.totalElements")
                                        .type(JsonFieldType.NUMBER).description("총 배너 개수"),
                                fieldWithPath("bannerList.totalPages")
                                        .type(JsonFieldType.NUMBER).description("총 페이지 수"),
                                fieldWithPath("bannerList.first")
                                        .type(JsonFieldType.BOOLEAN).description("첫번째 페이지 인지 여부"),
                                fieldWithPath("bannerList.numberOfElements")
                                        .type(JsonFieldType.NUMBER).description("현재 페이지에 있는 배너 개수"),
                                fieldWithPath("bannerList.size")
                                        .type(JsonFieldType.NUMBER).description("현재 조회 요청 개수"),
                                fieldWithPath("bannerList.number")
                                        .type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                                subsectionWithPath("bannerList.sort")
                                        .type(JsonFieldType.OBJECT).description("정렬 정보"),
                                fieldWithPath("bannerList.empty")
                                        .type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
                        )
                ));
    }

    @Test
    @Order(201)
    @DisplayName("전체 배너 리스트 테스트 - 성공 :: ")
    public void getBannerList() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/banner",
                                bannerSaveDto.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())

                // 문서화
                .andDo(document("banner-list-by-all",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                CampaignDocumentUtil.getCampaignParameterWithName_Size(),
                                CampaignDocumentUtil.getCampaignParameterWithName_Page(),
                                parameterWithName("sort").description(
                                                "- 배너 리스트 정렬 방식\n\n"
                                                        + "default 값은 최신 순\n\n"
                                                        + "- 사용 가능한 값:\n\n"
                                                        + "정렬 대상: date"
                                                        + "정렬 방향: ASC,DESC\n\n"
                                                        + "예) sort=date,DESC (최신 순)\n\n"
                                                        + "sort=date,ASC (마감 순)")
                                        .optional()
                                        .attributes(getSortFormat())
                        )
                ));
    }

    @Test
    @Order(300)
    @DisplayName("배너 삭제 테스트 - 성공 :: ")
    public void deleteBanner() throws Exception {
        bannerId = saveBanner();
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/banner/{email}/{bannerId}",
                                bannerSaveDto.getEmail(), bannerId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())

                // 문서화
                .andDo(document("banner-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("email").description("사용자 이메일"),
                                parameterWithName("bannerId").description("삭제할 배너 id")
                        )))
                .andDo(print()
                );
    }

    @Test
    @Order(400)
    @DisplayName("배너 수정 테스트 - 성공 :: ")
    public void modifyBanner() throws Exception {
        bannerId = saveBanner();
        mockMvc.perform(multipart("/api/banner/modify")
                        .file((MockMultipartFile) bannerSaveDto.getBannerImg())
                        .param("bannerId", bannerId)
                        .param("email", bannerSaveDto.getEmail())
                        .param("date", localDateTime)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())

                // 문서화
                .andDo(document("banner-modify",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                // 요청 필드 문서화
                                requestParts(
                                        partWithName("bannerImg").description("업로드 할 배너 이미지 파일")
                                ),
                                requestParameters(
                                        parameterWithName("bannerId").description("배너 아이디"),
                                        parameterWithName("email").description("사용자 이메일"),
                                        parameterWithName("date").description("배너 만든/수정한 시간\n\n"
                                                                                      + "형식: yyyy-MM-dd HH:mm:ss")
                                ),
                                // 응답 필드 문서화
                                relaxedResponseFields(
                                        fieldWithPath("banner.bannerId").description("배너 아이디"),
                                        fieldWithPath("banner.imgUrl").description("배너 이미지 주소"),
                                        fieldWithPath("banner.date").description("배너 만든/수정한 시간")
                                ))
                )
                .andDo(print()
                );
    }
}