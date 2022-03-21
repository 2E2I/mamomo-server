package com.hsu.mamomo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.hsu.mamomo.domain.Authority;
import com.hsu.mamomo.domain.User;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    private User user;
    private Authority authority;

    @BeforeEach
    void setData() throws ParseException {
        String id = Generators.randomBasedGenerator().generate().toString();

        System.out.println("id = " + id);

        String day = "20000101";
        Date date = new java.text.SimpleDateFormat("yyyyMMdd").parse(day);

        authority = Authority.builder()
                .authorityName("ROLE_USER")
                .build();

        user = User.builder()
                .id(id)
                .email("user@email.com")
                .password("user1234")
                .nickname("user1")
                .sex("M")
                .birth(date)
                .authorities(Collections.singleton(authority))
                .build();
    }

    @Test
    @DisplayName("회원가입 테스트")
    void signUpTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(post("/api/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

}