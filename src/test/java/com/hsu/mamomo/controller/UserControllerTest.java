package com.hsu.mamomo.controller;

import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentRequest;
import static com.hsu.mamomo.document.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.repository.jpa.UserRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
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
@TestMethodOrder(value = MethodOrderer.DisplayName.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static private User user;

    static private String jwtToken;

    static private String jwtToken;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private static UserRepository userRepository;

    @BeforeAll
    static void setData() {

        user = User.builder()
                .email("user@email.com")
                .password("user1234")
                .nickname("user1")
                .sex("M")
                .birth(LocalDate.of(2000, 1, 1))
                .build();
    }

    @Test
    @DisplayName("1. 회원가입")
    void signUpTest() throws Exception {

        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("2. 토큰 발급 (로그인)")
    void authenticationTest() throws Exception {

        Map<String, String> input = new HashMap<>();
        input.put("email", user.getEmail());
        input.put("password", user.getPassword());

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

    @DisplayName("3. 발급된 토큰으로 유저 정보 조회")
    @Test
    public void getUserInfoTest() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(RestDocumentationRequestBuilders.get("/api/user/{email}", user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken))
                .andExpect(status().isOk())

                .andDo(document("get-user-info-with-jwtToken",
                        pathParameters(
                                parameterWithName("email").description("조회할 유저의 이메일")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION)
                                        .description("api/user/authenticate 로 발급받은 조회할 유저의 토큰.\n"
                                                + "토큰 문자열 앞에 'Bearer '(공백 한 개 포함) 을 붙입니다.")
                        ),
                        responseFields(
                                fieldWithPath("id").description("유저 id"),
                                fieldWithPath("email").description("유저 이메일"),
                                fieldWithPath("password").description("null 리턴"),
                                fieldWithPath("nickname").description("유저 별명"),
                                fieldWithPath("sex").description("유저 성별"),
                                fieldWithPath("birth").description("유저 생년월일"),
                                fieldWithPath("profile").description("유저 프로필사진 url"),
                                fieldWithPath("create_date").description("회원가입 시간"),
                                fieldWithPath("modify_date").description("마지막 회원 정보 수정 시간"),
                                fieldWithPath("authorities").ignored(),
                                fieldWithPath("authorities.[].authorityName").ignored()
                        )))
                .andReturn();

        // 응답 바디의 jwt 토큰 추출
        String responseBody = mvcResult.getResponse().getContentAsString();
        User responseUser = objectMapper.readValue(responseBody, User.class);

        // 맞는 User를 반환했는지 검증
        assertThat(responseUser)
                .usingRecursiveComparison()
                .ignoringFields("id", "password", "create_date", "modify_date", "authorities")
                .ignoringFieldsOfTypes(User.class)
                .isEqualTo(user);
    }

    // findByEmail Null 리턴함.
    @AfterAll
    static void deleteUser() {
        Optional<User> selectUser = userRepository
                .findByEmail(user.getEmail()); // Optional is Empty
        selectUser.ifPresent(value -> userRepository.delete(value));
    }

}