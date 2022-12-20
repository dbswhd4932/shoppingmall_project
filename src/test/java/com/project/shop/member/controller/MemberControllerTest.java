//package com.project.shop.member.controller;
//
//import com.project.shop.member.controller.request.MemberEditRequest;
//import com.project.shop.member.controller.request.MemberSignupRequest;
//import com.project.shop.member.controller.response.MemberResponse;
//import com.project.shop.member.repository.MemberRepository;
//import com.project.shop.member.service.Impl.MemberServiceImpl;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.refEq;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//import static org.springframework.http.MediaType.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//@WebMvcTest(MemberController.class)
//@MockBean(JpaMetamodelMappingContext.class)
//class MemberControllerTest extends ControllerSetting {
//
//    @MockBean
//    MemberServiceImpl memberService;
//
//    @MockBean
//    MemberRepository memberRepository;
//
//    @Test
//    @DisplayName("회원 생성")
//    void memberSignUpTest() throws Exception {
//        //given
//        MemberSignupRequest memberSignupRequest = MemberSignupRequest.builder()
//                .loginId("loginId")
//                .name("홍길동")
//                .password("1234")
//                .zipcode("우편번호")
//                .detailAddress("상세주소")
//                .email("test@naver.com")
//                .phone("010-1234-1234-")
//                .build();
//
//        //when
//        mockMvc.perform(post("/api/members")
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(memberSignupRequest)))
//                .andExpect(status().isCreated());
//
//        //then
//        verify(memberService).memberSignup(any());
//
//    }
//
//    @Test
//    @DisplayName("회원 단건 조회")
//    void memberFindOneTest() throws Exception {
//        //given
//        MemberResponse memberResponse = MemberResponse.builder()
//                .loginId("loginId")
//                .name("홍길동")
//                .password("1234")
//                .zipcode("우편번호")
//                .detailAddress("상세주소")
//                .email("test@naver.com")
//                .phone("010-1234-1234-")
//                .build();
//
//        given(memberService.memberFindOne(1L)).willReturn(memberResponse);
//
//        //when then
//        mockMvc.perform(get("/api/members/{memberId}", 1L)
//                        .contentType(APPLICATION_JSON))
//                .andExpect(jsonPath("$.name").value("홍길동"))
//                .andExpect(status().isOk());
//
//    }
//
//    @Test
//    @DisplayName("회원 전체 조회")
//    void memberFindAllTest() throws Exception {
//        //given
//        MemberResponse memberResponse = MemberResponse.builder()
//                .loginId("loginId")
//                .name("홍길동")
//                .password("1234")
//                .zipcode("우편번호")
//                .detailAddress("상세주소")
//                .email("test@naver.com")
//                .phone("010-1234-1234-")
//                .build();
//
//        given(memberService.memberFindAll()).willReturn(List.of(memberResponse));
//
//        //when then
//        mockMvc.perform(get("/api/members")
//                        .contentType(APPLICATION_JSON))
//                .andExpect(jsonPath("$.[0].name").value("홍길동"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("회원 수정")
//    void memberEditTest() throws Exception {
//        //given
//        MemberEditRequest memberEditRequest = MemberEditRequest.builder()
//                .password("5678")
//                .zipcode("우편번호수정")
//                .detailAddress("상세주소수정")
//                .email("test@gmail.com")
//                .phone("010-4321-4321")
//                .build();
//
//        //when then
//        mockMvc.perform(put("/api/members/{memberId}", 1L)
//                        .contentType(APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(memberEditRequest)))
//                .andExpect(status().isOk());
//
//        verify(memberService).memberEdit(any(), refEq(memberEditRequest));
//    }
//
//    @Test
//    @DisplayName("회원 삭제")
//    void memberDeleteTest() throws Exception {
//        //given
//        //when then
//        mockMvc.perform(delete("/api/members/{memberId}", 1L)
//                        .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk());
//
//        verify(memberService).memberDelete(1L);
//    }
//
//}