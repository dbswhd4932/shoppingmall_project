package com.project.shop.member.service.Impl;

import com.project.shop.factory.MemberFactory;
import com.project.shop.member.domain.Member;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    MemberServiceImpl memberService;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 생성")
    void memberSignupTest() {
        //given
        MemberSignupRequest signupRequestDto = MemberFactory.createSignupRequestDto();
        //when
        memberService.memberSignup(signupRequestDto);
        //then
        verify(memberRepository).save(any());

    }

    @Test
    @DisplayName("회원 단건 조회")
    void memberFindOneTest() {
        //given
        Member member = MemberFactory.createMember();
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        //when
        MemberResponse findMemberResponse = memberService.memberFindOne(member.getId());
        //then
        assertThat(findMemberResponse.getLoginId()).isEqualTo(member.getLoginId());
    }

    @Test
    @DisplayName("회원 전체 조회")
    void memberFindAllTest() {
        //given
        Member member1 = MemberFactory.createMember();
        Member member2 = MemberFactory.createMember();
        given(memberRepository.findAll()).willReturn(List.of(member1,member2));
        //when
        List<MemberResponse> memberResponses = memberService.memberFindAll();
        //then
        assertThat(memberResponses.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("회원 수정")
    void memberEditTest() {
        //given
        Member member = MemberFactory.createMember();
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        MemberEditRequest memberEditRequest = MemberEditRequest.builder()
                .password("4321")
                .zipcode("321-321")
                .detailAddress("busan")
                .email("userEdit@naver.com")
                .phone("010-2222-2222")
                .build();
        //when
        memberService.memberEdit(member.getId(), memberEditRequest);
        //then
        assertThat(member.getEmail()).isEqualTo(memberEditRequest.getEmail());

    }

    @Test
    @DisplayName("회원 삭제")
    void memberDeleteTest() {
        //given
        Member member = MemberFactory.createMember();
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        //when
        memberService.memberDelete(member.getId());
        //then
        verify(memberRepository).delete(member);
    }
}