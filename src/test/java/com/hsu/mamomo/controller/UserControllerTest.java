package com.hsu.mamomo.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.hsu.mamomo.domain.Authority;
import com.hsu.mamomo.domain.User;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    static private User user;
    static private Authority authority;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @BeforeAll
    static void setData() {
        String id = Generators.randomBasedGenerator().generate().toString();

        System.out.println("id = " + id);

        user = User.builder()
                .id(id)
                .email("user@email.com")
                .password("user1234")
                .nickname("user1")
                .sex("M")
                .birth(LocalDate.of(2000,1,1))
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    void signUpTest() throws Exception {

        mockMvc.perform(post("/api/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 테스트")
    void authenticationTest() throws Exception {

        Map<String, String> input = new HashMap<>();
        input.put("email", "user@email.com");
        input.put("password", "user1234");

        MvcResult mvcResult = mockMvc.perform(post("/api/user/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andReturn();

        // 응답 바디의 jwt 토큰 추출
        String responseBody = mvcResult.getResponse().getContentAsString();
        TokenDto tokenDto = objectMapper.readValue(responseBody, TokenDto.class);

        // 발급된 토큰이 유효한 jwt 토큰인지 확인
        assertTrue(jwtTokenProvider.validateToken(tokenDto.getToken()));
    }

}