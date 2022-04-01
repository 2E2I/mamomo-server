package com.hsu.mamomo.controller;

import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getUserBirthFormat;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getUserEmailFormat;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getUserFavTopicFormat;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getUserNicknameFormat;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getUserPasswordFormat;
import static com.hsu.mamomo.document.DocumentFormatGenerator.getUserSexFormat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.dto.UserDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static private UserDto userDto;

    static private String jwtToken;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeAll
    static void setData() {

        userDto = UserDto.builder()
                .email("user@email.com")
                .password("user1234")
                .nickname("user1")
                .sex("M")
                .birth("2000-01-01")
                .favTopics(List.of(1, 2))
                .build();
    }

    /**
     * 회원가입 테스트
     */

    @Test
    @Order(100)
    @DisplayName("회원가입 테스트 - 성공")
    void signUpTest() throws Exception {

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())

                // 문서화
                .andDo(document("signup-success",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        // 요청 필드 문서화
                        requestFields(
                                fieldWithPath("email").description("회원 이메일").attributes(getUserEmailFormat()),
                                fieldWithPath("password").description("회원 비밀번호").attributes(getUserPasswordFormat()),
                                fieldWithPath("nickname").description("회원 닉네임").attributes(getUserNicknameFormat()),
                                fieldWithPath("sex").description("회원 성별").attributes(getUserSexFormat()),
                                fieldWithPath("birth").description("회원 생년월일").optional().attributes(getUserBirthFormat()),
                                fieldWithPath("favTopics").description("회원 관심 기부 분야").optional().attributes(getUserFavTopicFormat())
                        ),
                        // 응답 바디 문서화
                        responseFields(
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("password").description("회원 비밀번호"),
                                fieldWithPath("nickname").description("회원 닉네임"),
                                fieldWithPath("sex").description("회원 성별"),
                                fieldWithPath("birth").description("회원 생년월일"),
                                fieldWithPath("favTopics").description("회원 관심 기부 분야")
                        )))

                .andReturn();
    }

    @Test
    @Order(101)
    @DisplayName("회원가입 테스트 - 실패 :: 이메일 중복")
    void signUpConflictEmailFailTest() throws Exception {

        UserDto duplicatedEmailUser = UserDto.builder()
                .email("user@email.com")
                .password("user1234")
                .nickname("unique")
                .sex("M")
                .birth("2000-01-01")
                .build();

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatedEmailUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.message").value("이미 가입된 이메일입니다."));
    }

    @Test
    @Order(102)
    @DisplayName("회원가입 테스트 - 실패 :: 닉네임 중복")
    void signUpConflictNicknameFailTest() throws Exception {

        UserDto duplicatedNicknameUser = UserDto.builder()
                .email("unique@email.com")
                .password("user1234")
                .nickname("user1")
                .sex("M")
                .birth("2000-01-01")
                .build();

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatedNicknameUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_NICKNAME"))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 닉네임입니다."));
    }

    @Test
    @Order(103)
    @DisplayName("회원가입 테스트 - 실패 :: 객체 변환 실패")
    void signUpBadRequestAbsenceOfEssentialFieldFailTest() throws Exception {

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("WRONG_OBJECT"))
                .andExpect(jsonPath("$.message").value("객체 변환이 되지 않습니다. 옳은 형식을 보내주세요."));
    }

    @Test
    @Order(103)
    @DisplayName("회원가입 테스트 - 실패 :: 인자 형식 검증 실패")
    void signUpBadRequestInvalidFieldFailTest() throws Exception {

        UserDto invalidEmailFieldUser = UserDto.builder()
                .email("email")
                .password("user1234")
                .nickname("user1")
                .sex("M")
                .birth("2000-01-01")
                .build();

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmailFieldUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_FIELD"))
                .andExpect(jsonPath("$.message").value("이메일 형식에 맞지 않습니다."));
    }

    /**
     * 로그인 테스트
     */

    @Test
    @Order(200)
    @DisplayName("로그인 테스트 - 성공")
    void authenticationTest() throws Exception {

        Map<String, String> input = new HashMap<>();
        input.put("email", userDto.getEmail());
        input.put("password", userDto.getPassword());

        MvcResult mvcResult = mockMvc
                .perform(RestDocumentationRequestBuilders.post("/api/user/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())

                // 문서화
                .andDo(document("authenticate-user",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        // 요청 필드 문서화
                        requestFields(
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("password").description("회원 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("요청한 인증 정보가 유효하다면 JWT 토큰이 발급됩니다.")
                        ),
                        // 응답 바디 문서화
                        responseFields(
                                fieldWithPath("token")
                                        .description("요청한 인증 정보가 유효하다면 JWT 토큰이 발급됩니다.")
                        )))

                .andReturn();

        // 응답 바디의 jwt 토큰 추출
        String responseBody = mvcResult.getResponse().getContentAsString();
        TokenDto tokenDto = objectMapper.readValue(responseBody, TokenDto.class);
        jwtToken = tokenDto.getToken();
        System.out.println("jwtToken = " + jwtToken);

        // 발급된 토큰이 유효한 jwt 토큰인지 확인
        assertTrue(jwtTokenProvider.validateToken(tokenDto.getToken()));
    }

    /**
     * 유저 정보 조회 테스트
     */

    @Order(300)
    @DisplayName("유저 정보 조회 테스트 - 성공 :: ROLE_USER 권한이 있을 때")
    @Test
    public void getUserInfoTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(RestDocumentationRequestBuilders.get("/api/user/{email}",
                                userDto.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())

                // 문서화
                .andDo(document("get-user-info-with-jwtToken",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("email").description("조회할 유저의 이메일")
                        ),
                        // 요청 필드 문서화
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("api/user/authenticate 로 발급받은 조회할 유저의 토큰.\n"
                                                             + "토큰 문자열 앞에 'Bearer '(공백 한 개 포함) 을 붙입니다.")
                        ),
                        // 응답 바디 문서화
                        responseFields(
                                fieldWithPath("user.id").description("유저 id"),
                                fieldWithPath("user.email").description("유저 이메일"),
                                fieldWithPath("user.password").description("null 리턴"),
                                fieldWithPath("user.nickname").description("유저 별명"),
                                fieldWithPath("user.sex").description("유저 성별"),
                                fieldWithPath("user.birth").description("유저 생년월일"),
                                fieldWithPath("user.profile").description("유저 프로필사진 url"),
                                fieldWithPath("user.create_date").description("회원가입 시간"),
                                fieldWithPath("user.modify_date").description("마지막 회원 정보 수정 시간"),
                                fieldWithPath("user.authorities").ignored(),
                                fieldWithPath("user.authorities.[].authorityName").description("계정 정보"),
                                fieldWithPath("user.hearts").description("유저 좋아요 정보"),
                                fieldWithPath("user.hearts.[]").ignored(),
                                subsectionWithPath("user.favTopic").description("유저 관심 기부 분야"),
                                subsectionWithPath("user.favTopic.[].topic").description("기부 분야(카테고리) 정보")
                        )))
                .andDo(print())
                .andReturn();

        // 응답 바디의 jwt 토큰 추출
        String responseBody = mvcResult.getResponse().getContentAsString();
        User responseUser = objectMapper.readValue(responseBody, User.class);

        // 맞는 User를 반환했는지 검증
        assertThat(responseUser)
                .usingRecursiveComparison()
                .ignoringFields("id", "password", "create_date", "modify_date", "authorities",
                        "likes")
                .ignoringFieldsOfTypes(User.class)
                .isEqualTo(userDto);
    }

    /**
     * 회원탈퇴 테스트
     */

    @Order(400)
    @DisplayName("회원탈퇴 테스트 - 성공")
    @Test
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/api/user/{email}", userDto.getEmail()))
                .andExpect(status().isOk());
    }

    @Order(401)
    @DisplayName("회원탈퇴 테스트 - 실패 :: 존재하지 않는 회원일때")
    @Test
    public void deleteUserNotFoundFailTest() throws Exception {
        mockMvc.perform(delete("/api/user/{email}", userDto.getEmail()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("해당 유저 정보를 찾을 수 없습니다"));
    }

}