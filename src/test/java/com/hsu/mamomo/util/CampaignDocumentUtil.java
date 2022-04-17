package com.hsu.mamomo.util;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
        return parameterWithName("size").description("한 페이지당 캠페인 개수\n\n"
                                                             + "default 값은 20").optional();
    }

    static public ParameterDescriptor getCampaignParameterWithName_Page() {
        return parameterWithName("page").description("현재 페이지. 0부터 증가").optional();
    }

    static public ParameterDescriptor getCampaginParameterWithName_Category() {
        return parameterWithName("category").description(
                "- 캠페인 카테고리\n\n"
                        + "- 사용 가능한 값:\n\n"
                        + "1: 아동|청소년\n\n"
                        + "2: 어르신\n\n"
                        + "3: 장애인\n\n"
                        + "4: 어려운이웃\n\n"
                        + "5: 다문화\n\n"
                        + "6: 지구촌\n\n"
                        + "7: 가족|여성\n\n"
                        + "8: 우리사회\n\n"
                        + "9: 동물\n\n"
                        + "10: 환경");
    }

    static public ResponseFieldsSnippet getCampaignResponseFields() {
        return responseFields(
                fieldWithPath("campaigns.content").type(JsonFieldType.ARRAY)
                        .description("캠페인 리스트"),
                fieldWithPath("campaigns.content.[].id").type(JsonFieldType.STRING)
                        .description("캠페인 아이디"),
                fieldWithPath("campaigns.content.[].siteType").type(JsonFieldType.STRING)
                        .description("사이트 타입"),
                fieldWithPath("campaigns.content.[].url").type(JsonFieldType.STRING)
                        .description("URL 주소"),
                fieldWithPath("campaigns.content.[].title").type(JsonFieldType.STRING)
                        .description("제목"),
                fieldWithPath("campaigns.content.[].category").type(JsonFieldType.ARRAY)
                        .description("카테고리"),
                fieldWithPath("campaigns.content.[].tags").type(JsonFieldType.ARRAY)
                        .description("태그 모음"),
                fieldWithPath("campaigns.content.[].body").type(JsonFieldType.STRING)
                        .description("본문"),
                fieldWithPath("campaigns.content.[].organizationName").type(
                        JsonFieldType.STRING).description("조직 기관"),
                fieldWithPath("campaigns.content.[].thumbnail").type(JsonFieldType.STRING)
                        .description("썸네일"),
                fieldWithPath("campaigns.content.[].dueDate").type(JsonFieldType.STRING)
                        .description("마감 날짜"),
                fieldWithPath("campaigns.content.[].startDate").type(JsonFieldType.STRING)
                        .description("시작 날짜"),
                fieldWithPath("campaigns.content.[].targetPrice").type(JsonFieldType.NUMBER)
                        .description("목표 금액"),
                fieldWithPath("campaigns.content.[].statusPrice").type(JsonFieldType.NUMBER)
                        .description("현재 금액"),
                fieldWithPath("campaigns.content.[].percent").type(JsonFieldType.NUMBER)
                        .description("달성 정도"),
                fieldWithPath("campaigns.content.[].isHeart").type(JsonFieldType.BOOLEAN)
                        .description("좋아요 여부"),
                fieldWithPath("campaigns.content.[].heartCount").type(JsonFieldType.NUMBER)
                        .description("좋아요 갯수"),
                fieldWithPath("campaigns.pageable")
                        .type(JsonFieldType.OBJECT).description("페이지 정보"),
                subsectionWithPath("campaigns.pageable.sort")
                        .type(JsonFieldType.OBJECT).description("정렬 정보"),
                fieldWithPath("campaigns.pageable.offset")
                        .type(JsonFieldType.NUMBER).description("현재 offset"),
                fieldWithPath("campaigns.pageable.pageNumber")
                        .type(JsonFieldType.NUMBER).description("현재 페이지"),
                fieldWithPath("campaigns.pageable.pageSize")
                        .type(JsonFieldType.NUMBER).description("페이지 하나의 크기"),
                fieldWithPath("campaigns.pageable.paged")
                        .type(JsonFieldType.BOOLEAN).description("페이지 처리가 되었는지 여부"),
                fieldWithPath("campaigns.pageable.unpaged")
                        .type(JsonFieldType.BOOLEAN).description("페이지 처리가 안되었는지 여부"),
                fieldWithPath("campaigns.last")
                        .type(JsonFieldType.BOOLEAN).description("마지막 페이지 인지 여부"),
                fieldWithPath("campaigns.totalElements")
                        .type(JsonFieldType.NUMBER).description("총 캠페인 개수"),
                fieldWithPath("campaigns.totalPages")
                        .type(JsonFieldType.NUMBER).description("총 페이지 수"),
                fieldWithPath("campaigns.first")
                        .type(JsonFieldType.BOOLEAN).description("첫번째 페이지 인지 여부"),
                fieldWithPath("campaigns.numberOfElements")
                        .type(JsonFieldType.NUMBER).description("현재 페이지에 있는 캠페인 개수"),
                fieldWithPath("campaigns.size")
                        .type(JsonFieldType.NUMBER).description("현재 조회 요청 개수"),
                fieldWithPath("campaigns.number")
                        .type(JsonFieldType.NUMBER).description("현재 페이지 번호"),
                subsectionWithPath("campaigns.sort")
                        .type(JsonFieldType.OBJECT).description("정렬 정보"),
                fieldWithPath("campaigns.empty")
                        .type(JsonFieldType.BOOLEAN).description("비어있는지 여부")
        );
    }

}
