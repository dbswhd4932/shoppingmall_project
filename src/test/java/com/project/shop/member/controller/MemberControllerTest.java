package com.project.shop.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shop.factory.MemberFactory;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.MemberSignupRequest;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.Impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    MemberController memberController;

    @Mock
    MemberServiceImpl memberService;

    @Mock
    MemberRepository memberRepository;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    @DisplayName("회원 생성")
    void memberSignupTest() throws Exception {
        //given
        MemberSignupRequest signupRequestDto = MemberFactory.createSignupRequestDto();
        Member member = Member.create(signupRequestDto);

        //when //then
        mockMvc.perform(post("/api/members")
                        .content(objectMapper.writeValueAsString(member))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("회원 단건 조회")
    void memberFindOneTest() throws Exception {
        //given
        Member member = MemberFactory.createMember();

        //when  //then
        mockMvc.perform(get("/api/members/{memberId}", member.getId()))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("회원 전체 조회")
    void memberFindAllTesT() {
    }

}