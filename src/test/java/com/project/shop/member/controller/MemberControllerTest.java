package com.project.shop.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shop.factory.MemberFactory;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.MemberSignupDto;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
//@WebMvcTest(MemberController.class)
//@SpringBootTest
//@AutoConfigureMockMvc
class MemberControllerTest {

    @Mock
    MemberService memberService;
    MockMvc mockMvc;

    @BeforeEach
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(new MemberController(memberService)).build();
    }

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("회원가입 테스트")
    void signupTest() throws Exception {
        //given
        MemberSignupDto signupRequestDto = MemberFactory.createSignupRequestDto();
        Member member = new Member(signupRequestDto);

        mockMvc.perform(post("/api/members")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andDo(print());
    }
}