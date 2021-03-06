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
        input.put("email", "bannerTest@email.com");
        input.put("password", "password");

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

    @BeforeEach
    public void setBannerDto() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        localDateTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        bannerSaveDto = BannerSaveDto.builder()
                .email("bannerTest@email.com")
                .originalImgData(new MockMultipartFile("originalImgData",
                        "originalTest.jpg",
                        "image/jpeg",
                        new FileInputStream("src/test/resources/originalBannerTest.jpg")))
                .imgData(new MockMultipartFile("imgData",
                        "test.jpg",
                        "image/jpeg",
                        new FileInputStream("src/test/resources/bannerTest.jpg")))
                .url("https://github.com/2E2I/mamomo-server")
                .date(now)
                .siteType("SiteType")
                .title("Title")
                .info("Info(")
                .width("Width")
                .height("Height")
                .bgColor1("BgColor1")
                .bgColor2("BgColor2")
                .textColor1("TextColor1")
                .textColor2("TextColor2")
                .textColor3("TextColor3")
                .textFont1("TextFont1")
                .textFont2("TextFont2")
                .textFont3("TextFont3")
                .build();
        userId = "6a79273a-6c30-4615-b927-3a6c30d6150c";
    }

    @Test
    @Order(100)
    @DisplayName("?????? ?????? ????????? - ?????? :: ")
    public String saveBanner() throws Exception {
        MvcResult mvcResult = mockMvc.perform(multipart("/api/banner")
                        .file((MockMultipartFile) bannerSaveDto.getImgData())
                        .file((MockMultipartFile) bannerSaveDto.getOriginalImgData())
                        .param("email", bannerSaveDto.getEmail())
                        .param("url", bannerSaveDto.getUrl())
                        .param("date", localDateTime)
                        .param("siteType", bannerSaveDto.getSiteType())
                        .param("title", bannerSaveDto.getTitle())
                        .param("info", bannerSaveDto.getInfo())
                        .param("width", bannerSaveDto.getWidth())
                        .param("height", bannerSaveDto.getHeight())
                        .param("bgColor1", bannerSaveDto.getBgColor1())
                        .param("bgColor2", bannerSaveDto.getBgColor2())
                        .param("textColor1", bannerSaveDto.getTextColor1())
                        .param("textColor2", bannerSaveDto.getTextColor2())
                        .param("textColor3", bannerSaveDto.getTextColor3())
                        .param("textFont1", bannerSaveDto.getTextFont1())
                        .param("textFont2", bannerSaveDto.getTextFont2())
                        .param("textFont3", bannerSaveDto.getTextFont3())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))

                .andExpect(status().isOk())

                // document
                .andDo(document("banner-save",
                        getDocumentRequest(),
                        getDocumentResponse(),

                        // ?????? ?????? ?????????
                        requestParts(
                                partWithName("originalImgData").description("?????? ????????? ????????? ??????\n\n"
                                                                                    + "File ???????????????."),
                                partWithName("imgData").description("????????? ??? ?????? ????????? ??????\n\n"
                                                                            + "File ???????????????.")
                        ),
                        requestParameters(
                                parameterWithName("email").description("????????? ?????????"),
                                parameterWithName("url").description("?????? ????????? url"),
                                parameterWithName("date").description("?????? ??????/????????? ??????\n\n"
                                                                              + "?????? [yyyy-MM-dd HH:mm:ss]?????????."),
                                parameterWithName("siteType").description("?????? ????????? ??????\n\n"
                                                                                  + "??????: String"),
                                parameterWithName("title").description("?????? ??????"
                                                                               + "??????: String"),
                                parameterWithName("info").description("?????? ??????"
                                                                              + "??????: String"),
                                parameterWithName("width").description("?????? ??????"
                                                                               + "??????: String"),
                                parameterWithName("height").description("?????? ??????"
                                                                                + "??????: String"),
                                parameterWithName("bgColor1").description("?????? ?????? ??????1"
                                                                                  + "??????: String"),
                                parameterWithName("bgColor2").description("?????? ?????? ??????2"
                                                                                  + "??????: String"),
                                parameterWithName("textColor1").description("?????? ????????? ??????1"
                                                                                    + "??????: String"),
                                parameterWithName("textColor2").description("?????? ????????? ??????2"
                                                                                    + "??????: String"),
                                parameterWithName("textColor3").description("?????? ????????? ??????3"
                                                                                    + "??????: String"),
                                parameterWithName("textFont1").description("?????? ????????? ??????1"
                                                                                   + "??????: String"),
                                parameterWithName("textFont2").description("?????? ????????? ??????2"
                                                                                   + "??????: String"),
                                parameterWithName("textFont3").description("?????? ????????? ??????3"
                                                                                   + "??????: String")
                        ),
                        // ?????? ?????? ?????????
                        relaxedResponseFields(
                                fieldWithPath("banner.bannerId").description("?????? ?????????"),
                                fieldWithPath("banner.img").description("?????? ????????? ??????"),
                                fieldWithPath("banner.originalImg").description("?????? ????????? ????????? ??????"),
                                fieldWithPath("banner.url").description("?????? ????????? url").optional(),
                                fieldWithPath("banner.date").description("?????? ??????/????????? ??????"),
                                fieldWithPath("banner.siteType").description("?????? ????????? ??????"),
                                fieldWithPath("banner.title").description("?????? ??????"),
                                fieldWithPath("banner.info").description("?????? ??????"),
                                fieldWithPath("banner.width").description("?????? ??????"),
                                fieldWithPath("banner.height").description("?????? ??????"),
                                fieldWithPath("banner.bgColor1").description("?????? ?????? ??????1"),
                                fieldWithPath("banner.bgColor2").description("?????? ?????? ??????2"),
                                fieldWithPath("banner.textColor1").description("?????? ????????? ??????1"),
                                fieldWithPath("banner.textColor2").description("?????? ????????? ??????2"),
                                fieldWithPath("banner.textColor3").description("?????? ????????? ??????3"),
                                fieldWithPath("banner.textFont1").description("?????? ????????? ??????1"),
                                fieldWithPath("banner.textFont2").description("?????? ????????? ??????2"),
                                fieldWithPath("banner.textFont3").description("?????? ????????? ??????3")
                        )
                ))
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        BannerDto responseBanner = objectMapper.readValue(responseBody, BannerDto.class);
        bannerId = responseBanner.getBanner().getBannerId();

        return bannerId;
    }

    @Test
    @Order(200)
    @DisplayName("?????? ?????? ????????? ????????? - ?????? :: ")
    public String getBannerListByUser() throws Exception {
        bannerId = saveBanner();
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/banner/{email}",
                                bannerSaveDto.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())

                // ?????????
                .andDo(document("banner-list-by-user",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("email").description("????????? ?????????"),
                                CampaignDocumentUtil.getCampaignParameterWithName_Size(),
                                CampaignDocumentUtil.getCampaignParameterWithName_Page(),
                                parameterWithName("sort").description(
                                                "- ?????? ????????? ?????? ??????\n\n"
                                                        + "default ?????? ?????? ???\n\n"
                                                        + "- ?????? ????????? ???:\n\n"
                                                        + "?????? ??????: date\n\n"
                                                        + "?????? ??????: ASC,DESC\n\n"
                                                        + "???) sort=date,DESC (?????? ???)\n\n"
                                                        + "sort=date,ASC (?????? ???)")
                                        .optional()
                                        .attributes(getSortFormat())
                        ),
                        relaxedResponseFields(
                                fieldWithPath("bannerList.content").description("?????? ?????????"),
                                fieldWithPath("bannerList.content.[].bannerId").description(
                                        "?????? ?????????"),
                                fieldWithPath("bannerList.content.[].img").description(
                                        "?????? ????????? url"),
                                fieldWithPath("bannerList.content.[].originalImg").description(
                                        "?????? ????????? ????????? ??????\n\n"
                                                + "(Base64 ????????? ???)"),
                                fieldWithPath("bannerList.content.[].url").optional().description("?????? ????????? url"),
                                fieldWithPath("bannerList.content.[].date").description(
                                        "?????? ??????/????????? ??????"),
                                fieldWithPath("bannerList.content.[].siteType").description(
                                        "?????? ????????? ??????"),
                                fieldWithPath("bannerList.content.[].title").description("?????? ??????"),
                                fieldWithPath("bannerList.content.[].info").description("?????? ??????"),
                                fieldWithPath("bannerList.content.[].width").description("?????? ??????"),
                                fieldWithPath("bannerList.content.[].height").description("?????? ??????"),
                                fieldWithPath("bannerList.content.[].bgColor1").description(
                                        "?????? ?????? ??????1"),
                                fieldWithPath("bannerList.content.[].bgColor2").description(
                                        "?????? ?????? ??????2"),
                                fieldWithPath("bannerList.content.[].textColor1").description(
                                        "?????? ????????? ??????1"),
                                fieldWithPath("bannerList.content.[].textColor2").description(
                                        "?????? ????????? ??????2"),
                                fieldWithPath("bannerList.content.[].textColor3").description(
                                        "?????? ????????? ??????3"),
                                fieldWithPath("bannerList.content.[].textFont1").description(
                                        "?????? ????????? ??????1"),
                                fieldWithPath("bannerList.content.[].textFont2").description(
                                        "?????? ????????? ??????2"),
                                fieldWithPath("bannerList.content.[].textFont3").description(
                                        "?????? ????????? ??????3"),
                                fieldWithPath("bannerList.pageable")
                                        .type(JsonFieldType.OBJECT).description("????????? ??????"),
                                subsectionWithPath("bannerList.pageable.sort")
                                        .type(JsonFieldType.OBJECT).description("?????? ??????"),
                                fieldWithPath("bannerList.pageable.offset")
                                        .type(JsonFieldType.NUMBER).description("?????? offset"),
                                fieldWithPath("bannerList.pageable.pageNumber")
                                        .type(JsonFieldType.NUMBER).description("?????? ?????????"),
                                fieldWithPath("bannerList.pageable.pageSize")
                                        .type(JsonFieldType.NUMBER).description("????????? ????????? ??????"),
                                fieldWithPath("bannerList.pageable.paged")
                                        .type(JsonFieldType.BOOLEAN).description("????????? ????????? ???????????? ??????"),
                                fieldWithPath("bannerList.pageable.unpaged")
                                        .type(JsonFieldType.BOOLEAN)
                                        .description("????????? ????????? ??????????????? ??????"),
                                fieldWithPath("bannerList.last")
                                        .type(JsonFieldType.BOOLEAN).description("????????? ????????? ?????? ??????"),
                                fieldWithPath("bannerList.totalElements")
                                        .type(JsonFieldType.NUMBER).description("??? ?????? ??????"),
                                fieldWithPath("bannerList.totalPages")
                                        .type(JsonFieldType.NUMBER).description("??? ????????? ???"),
                                fieldWithPath("bannerList.first")
                                        .type(JsonFieldType.BOOLEAN).description("????????? ????????? ?????? ??????"),
                                fieldWithPath("bannerList.numberOfElements")
                                        .type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ?????? ??????"),
                                fieldWithPath("bannerList.size")
                                        .type(JsonFieldType.NUMBER).description("?????? ?????? ?????? ??????"),
                                fieldWithPath("bannerList.number")
                                        .type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                                subsectionWithPath("bannerList.sort")
                                        .type(JsonFieldType.OBJECT).description("?????? ??????"),
                                fieldWithPath("bannerList.empty")
                                        .type(JsonFieldType.BOOLEAN).description("??????????????? ??????")
                        )
                ));
        return bannerId;
    }

    @Test
    @Order(201)
    @DisplayName("?????? ?????? ????????? ????????? - ?????? :: ")
    public String getBannerList() throws Exception {
        bannerId = getBannerListByUser();
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/banner",
                                bannerSaveDto.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())

                // ?????????
                .andDo(document("banner-list-by-all",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                CampaignDocumentUtil.getCampaignParameterWithName_Size(),
                                CampaignDocumentUtil.getCampaignParameterWithName_Page(),
                                parameterWithName("sort").description(
                                                "- ?????? ????????? ?????? ??????\n\n"
                                                        + "default ?????? ?????? ???\n\n"
                                                        + "- ?????? ????????? ???:\n\n"
                                                        + "?????? ??????: date"
                                                        + "?????? ??????: ASC,DESC\n\n"
                                                        + "???) sort=date,DESC (?????? ???)\n\n"
                                                        + "sort=date,ASC (?????? ???)")
                                        .optional()
                                        .attributes(getSortFormat())
                        )
                ));

        return bannerId;
    }

    @Test
    @Order(300)
    @DisplayName("?????? ?????? ????????? - ?????? :: ")
    public String getBannerStatus() throws Exception {
        bannerId = getBannerList();
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/banner/status")
                        .param("bannerId", bannerId)
                        .param("email", bannerSaveDto.getEmail())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())

                // ?????????
                .andDo(document("banner-status",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        // ?????? ?????? ?????????
                        requestParameters(
                                parameterWithName("bannerId").description("?????? ?????????"),
                                parameterWithName("email").description("????????? ?????????")
                        ),
                        // ?????? ?????? ?????????
                        relaxedResponseFields(
                                fieldWithPath("banner.bannerId").description("?????? ?????????"),
                                fieldWithPath("banner.img").description("?????? ????????? ??????"),
                                fieldWithPath("banner.originalImg").description("?????? ????????? ????????? ??????\n\n"
                                                                                        + "(Base64 ????????? ???)"),
                                fieldWithPath("banner.url").description("?????? ????????? url").optional(),
                                fieldWithPath("banner.date").description("?????? ??????/????????? ??????"),
                                fieldWithPath("banner.siteType").description("?????? ????????? ??????"),
                                fieldWithPath("banner.title").description("?????? ??????"),
                                fieldWithPath("banner.info").description("?????? ??????"),
                                fieldWithPath("banner.width").description("?????? ??????"),
                                fieldWithPath("banner.height").description("?????? ??????"),
                                fieldWithPath("banner.bgColor1").description("?????? ?????? ??????1"),
                                fieldWithPath("banner.bgColor2").description("?????? ?????? ??????2"),
                                fieldWithPath("banner.textColor1").description("?????? ????????? ??????1"),
                                fieldWithPath("banner.textColor2").description("?????? ????????? ??????2"),
                                fieldWithPath("banner.textColor3").description("?????? ????????? ??????3"),
                                fieldWithPath("banner.textFont1").description("?????? ????????? ??????1"),
                                fieldWithPath("banner.textFont2").description("?????? ????????? ??????2"),
                                fieldWithPath("banner.textFont3").description("?????? ????????? ??????3")
                        ))
                )
                .andDo(print()
                );
        return bannerId;
    }

    @Test
    @Order(301)
    @DisplayName("?????? ?????? ????????? - ?????? :: ")
    public String modifyBanner() throws Exception {
        bannerId = getBannerStatus();
        mockMvc.perform(multipart("/api/banner/modify")
                        .file((MockMultipartFile) bannerSaveDto.getImgData())
                        .param("bannerId", bannerId)
                        .param("email", bannerSaveDto.getEmail())
                        .param("url", bannerSaveDto.getUrl())
                        .param("date", localDateTime)
                        .param("siteType", bannerSaveDto.getSiteType())
                        .param("title", bannerSaveDto.getTitle())
                        .param("info", bannerSaveDto.getInfo())
                        .param("width", bannerSaveDto.getWidth())
                        .param("height", bannerSaveDto.getHeight())
                        .param("bgColor1", bannerSaveDto.getBgColor1())
                        .param("bgColor2", bannerSaveDto.getBgColor2())
                        .param("textColor1", bannerSaveDto.getTextColor1())
                        .param("textColor2", bannerSaveDto.getTextColor2())
                        .param("textColor3", bannerSaveDto.getTextColor3())
                        .param("textFont1", bannerSaveDto.getTextFont1())
                        .param("textFont2", bannerSaveDto.getTextFont2())
                        .param("textFont3", bannerSaveDto.getTextFont3())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isOk())

                // ?????????
                .andDo(document("banner-modify",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        // ?????? ?????? ?????????
                        requestParts(
                                partWithName("imgData").description("????????? ??? ?????? ????????? ??????\n\n"
                                                                            + "File ???????????????.")
                        ),
                        requestParameters(
                                parameterWithName("bannerId").description("?????? ?????????"),
                                parameterWithName("email").description("????????? ?????????"),
                                parameterWithName("url").description("?????? ????????? url").optional(),
                                parameterWithName("date").description("?????? ??????/????????? ??????\n\n"
                                                                              + "?????? [yyyy-MM-dd HH:mm:ss]?????????."),
                                parameterWithName("siteType").description("?????? ????????? ??????\n\n"
                                                                                  + "??????: String"),
                                parameterWithName("title").description("?????? ??????"
                                                                               + "??????: String"),
                                parameterWithName("info").description("?????? ??????"
                                                                              + "??????: String"),
                                parameterWithName("width").description("?????? ??????"
                                                                               + "??????: String"),
                                parameterWithName("height").description("?????? ??????"
                                                                                + "??????: String"),
                                parameterWithName("bgColor1").description("?????? ?????? ??????1"
                                                                                  + "??????: String"),
                                parameterWithName("bgColor2").description("?????? ?????? ??????2"
                                                                                  + "??????: String"),
                                parameterWithName("textColor1").description("?????? ????????? ??????1"
                                                                                    + "??????: String"),
                                parameterWithName("textColor2").description("?????? ????????? ??????2"
                                                                                    + "??????: String"),
                                parameterWithName("textColor3").description("?????? ????????? ??????3"
                                                                                    + "??????: String"),
                                parameterWithName("textFont1").description("?????? ????????? ??????1"
                                                                                   + "??????: String"),
                                parameterWithName("textFont2").description("?????? ????????? ??????2"
                                                                                   + "??????: String"),
                                parameterWithName("textFont3").description("?????? ????????? ??????3"
                                                                                   + "??????: String")
                        ),
                        // ?????? ?????? ?????????
                        relaxedResponseFields(
                                fieldWithPath("banner.bannerId").description("?????? ?????????"),
                                fieldWithPath("banner.img").description("?????? ????????? ??????"),
                                fieldWithPath("banner.originalImg").description("?????? ????????? ????????? ??????"),
                                fieldWithPath("banner.url").description("?????? ????????? url").optional(),
                                fieldWithPath("banner.date").description("?????? ??????/????????? ??????"),
                                fieldWithPath("banner.siteType").description("?????? ????????? ??????"),
                                fieldWithPath("banner.title").description("?????? ??????"),
                                fieldWithPath("banner.info").description("?????? ??????"),
                                fieldWithPath("banner.width").description("?????? ??????"),
                                fieldWithPath("banner.height").description("?????? ??????"),
                                fieldWithPath("banner.bgColor1").description("?????? ?????? ??????1"),
                                fieldWithPath("banner.bgColor2").description("?????? ?????? ??????2"),
                                fieldWithPath("banner.textColor1").description("?????? ????????? ??????1"),
                                fieldWithPath("banner.textColor2").description("?????? ????????? ??????2"),
                                fieldWithPath("banner.textColor3").description("?????? ????????? ??????3"),
                                fieldWithPath("banner.textFont1").description("?????? ????????? ??????1"),
                                fieldWithPath("banner.textFont2").description("?????? ????????? ??????2"),
                                fieldWithPath("banner.textFont3").description("?????? ????????? ??????3")
                        ))
                )
                .andDo(print()
                );

        return bannerId;
    }

    @Test
    @Order(400)
    @DisplayName("?????? ?????? ?????????")
    public void deleteBanner() throws Exception {
        bannerId = modifyBanner();
        mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/banner/{email}/{bannerId}",
                                bannerSaveDto.getEmail(), bannerId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())

                // ?????????
                .andDo(document("banner-delete",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("email").description("????????? ?????????"),
                                parameterWithName("bannerId").description("????????? ?????? id")
                        )))
                .andDo(print()
                );
    }

}