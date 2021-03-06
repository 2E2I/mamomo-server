package com.hsu.mamomo.util;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.web.servlet.ResultActions;

public class CampaignDocumentUtil {

    static public ResultActions getCampaignExpect(ResultActions resultActions) throws Exception {
        return resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campaigns.content").isArray())
                .andExpect(jsonPath("$.campaigns.content.[0].['id']").isString())
                .andExpect(jsonPath("$.campaigns.content.[0].['siteType']").isString())
                .andExpect(jsonPath("$.campaigns.content.[0].['url']").isString())
                .andExpect(jsonPath("$.campaigns.content.[0].['title']").isString())
                .andExpect(jsonPath("$.campaigns.content.[0].['category']").isArray())
                .andExpect(jsonPath("$.campaigns.content.[0].['tags']").isArray())
                .andExpect(jsonPath("$.campaigns.content.[0].['body']").isString())
                .andExpect(jsonPath("$.campaigns.content.[0].['organizationName']").isString())
                .andExpect(jsonPath("$.campaigns.content.[0].['thumbnail']").isString())
                .andExpect(jsonPath("$.campaigns.content.[0].['dueDate']").isString())
                .andExpect(jsonPath("$.campaigns.content.[0].['startDate']").isString())
                .andExpect(jsonPath("$.campaigns.content.[0].['targetPrice']").isNumber())
                .andExpect(jsonPath("$.campaigns.content.[0].['statusPrice']").isNumber())
                .andExpect(jsonPath("$.campaigns.content.[0].['percent']").isNumber())
                .andExpect(jsonPath("$.campaigns.content.[0].['isHeart']").isBoolean())
                .andExpect(jsonPath("$.campaigns.content.[0].['heartCount']").isNumber());
    }

    static public ParameterDescriptor getCampaignParameterWithName_Size() {
        return parameterWithName("size").description("??? ???????????? ????????? ??????\n\n"
                                                             + "default ?????? 20").optional();
    }

    static public ParameterDescriptor getCampaignParameterWithName_Page() {
        return parameterWithName("page").description("?????? ?????????. 0?????? ??????").optional();
    }

    static public ParameterDescriptor getCampaginParameterWithName_Category() {
        return parameterWithName("category").description(
                "- ????????? ????????????\n\n"
                        + "- ?????? ????????? ???:\n\n"
                        + "1: ??????|?????????\n\n"
                        + "2: ?????????\n\n"
                        + "3: ?????????\n\n"
                        + "4: ???????????????\n\n"
                        + "5: ?????????\n\n"
                        + "6: ?????????\n\n"
                        + "7: ??????|??????\n\n"
                        + "8: ????????????\n\n"
                        + "9: ??????\n\n"
                        + "10: ??????");
    }

    static public ResponseFieldsSnippet getCampaignResponseFields() {
        return relaxedResponseFields(
                fieldWithPath("campaigns.content").type(JsonFieldType.ARRAY)
                        .description("????????? ?????????"),
                fieldWithPath("campaigns.content.[].id").type(JsonFieldType.STRING)
                        .description("????????? ?????????"),
                fieldWithPath("campaigns.content.[].siteType").type(JsonFieldType.STRING)
                        .description("????????? ??????"),
                fieldWithPath("campaigns.content.[].url").type(JsonFieldType.STRING)
                        .description("URL ??????"),
                fieldWithPath("campaigns.content.[].title").type(JsonFieldType.STRING)
                        .description("??????"),
                fieldWithPath("campaigns.content.[].category").type(JsonFieldType.ARRAY)
                        .description("????????????"),
                fieldWithPath("campaigns.content.[].tags").type(JsonFieldType.ARRAY)
                        .description("?????? ??????"),
                fieldWithPath("campaigns.content.[].body").type(JsonFieldType.STRING)
                        .description("??????"),
                fieldWithPath("campaigns.content.[].organizationName").type(
                        JsonFieldType.STRING).description("?????? ??????"),
                fieldWithPath("campaigns.content.[].thumbnail").type(JsonFieldType.STRING)
                        .description("?????????"),
                fieldWithPath("campaigns.content.[].dueDate").type(JsonFieldType.STRING)
                        .description("?????? ??????"),
                fieldWithPath("campaigns.content.[].startDate").type(JsonFieldType.STRING)
                        .description("?????? ??????"),
                fieldWithPath("campaigns.content.[].targetPrice").type(JsonFieldType.NUMBER)
                        .description("?????? ??????"),
                fieldWithPath("campaigns.content.[].statusPrice").type(JsonFieldType.NUMBER)
                        .description("?????? ??????"),
                fieldWithPath("campaigns.content.[].percent").type(JsonFieldType.NUMBER)
                        .description("?????? ??????"),
                fieldWithPath("campaigns.content.[].isHeart").type(JsonFieldType.BOOLEAN)
                        .description("????????? ??????"),
                fieldWithPath("campaigns.content.[].heartCount").type(JsonFieldType.NUMBER)
                        .description("????????? ??????"),
                fieldWithPath("campaigns.pageable")
                        .type(JsonFieldType.OBJECT).description("????????? ??????"),
                subsectionWithPath("campaigns.pageable.sort")
                        .type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("campaigns.pageable.offset")
                        .type(JsonFieldType.NUMBER).description("?????? offset"),
                fieldWithPath("campaigns.pageable.pageNumber")
                        .type(JsonFieldType.NUMBER).description("?????? ?????????"),
                fieldWithPath("campaigns.pageable.pageSize")
                        .type(JsonFieldType.NUMBER).description("????????? ????????? ??????"),
                fieldWithPath("campaigns.pageable.paged")
                        .type(JsonFieldType.BOOLEAN).description("????????? ????????? ???????????? ??????"),
                fieldWithPath("campaigns.pageable.unpaged")
                        .type(JsonFieldType.BOOLEAN).description("????????? ????????? ??????????????? ??????"),
                fieldWithPath("campaigns.last")
                        .type(JsonFieldType.BOOLEAN).description("????????? ????????? ?????? ??????"),
                fieldWithPath("campaigns.totalElements")
                        .type(JsonFieldType.NUMBER).description("??? ????????? ??????"),
                fieldWithPath("campaigns.totalPages")
                        .type(JsonFieldType.NUMBER).description("??? ????????? ???"),
                fieldWithPath("campaigns.first")
                        .type(JsonFieldType.BOOLEAN).description("????????? ????????? ?????? ??????"),
                fieldWithPath("campaigns.numberOfElements")
                        .type(JsonFieldType.NUMBER).description("?????? ???????????? ?????? ????????? ??????"),
                fieldWithPath("campaigns.size")
                        .type(JsonFieldType.NUMBER).description("?????? ?????? ?????? ??????"),
                fieldWithPath("campaigns.number")
                        .type(JsonFieldType.NUMBER).description("?????? ????????? ??????"),
                subsectionWithPath("campaigns.sort")
                        .type(JsonFieldType.OBJECT).description("?????? ??????"),
                fieldWithPath("campaigns.empty")
                        .type(JsonFieldType.BOOLEAN).description("??????????????? ??????")
        );
    }

}
