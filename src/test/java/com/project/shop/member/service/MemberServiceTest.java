package com.project.shop.member.service;

import com.project.shop.factory.MemberFactory;
import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.domain.entity.Address;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.MemberSignupDto;
import com.project.shop.member.domain.request.MemberUpdateDto;
import com.project.shop.member.domain.response.MemberResponseDto;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원가입 테스트")
    void signupTest() {
        //given
        MemberSignupDto memberSignupDto = MemberFactory.createSignupRequestDto();
        Member member = new Member(memberSignupDto);
        given(memberRepository.save(any())).willReturn(member);
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        //when
        memberService.signup(memberSignupDto);

        //then
        assertThat(memberRepository.findById(member.getId())).isEqualTo(Optional.of(member));
        verify(memberRepository, times(1)).save(any());
        verify(memberRepository, times(1)).findById(any());
    }

    /* 질문하기
    @Test
    @DisplayName("회원가입 LoginId 중복 테스트")
    void signupDuplicatedLoginIdTest() {
        MemberSignupDto memberA = MemberSignupDto.builder().loginId("MemberA").email("memberA@aaa.com").build();

        given(memberService.signup(memberA)).willThrow(new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID));

        assertThrows(BusinessException.class, () -> {
            memberService.signup(memberA);
        });
    }
    */

    @Test
    @DisplayName("회원전체조회 테스트")
    void findAllTest() {
        //given
        List<MemberSignupDto> list = Arrays.asList(
                MemberFactory.createSignupRequestDto(),
                MemberFactory.createSignupRequestDto());

        for (MemberSignupDto memberSignupDto : list) {
            memberService.signup(memberSignupDto);
        }
        //when
        given(memberRepository.findAll()).willReturn(list.stream().map(signupRequestDto ->
                new Member(signupRequestDto)).collect(Collectors.toList()));
        //then
        assertThat(memberRepository.findAll().size()).isEqualTo(2);

        verify(memberRepository, times(1)).findAll();

    }

    @Test
    @DisplayName("회원단건조회 테스트")
    void findOneTest() {
        //given
        MemberSignupDto memberSignupDto = MemberFactory.createSignupRequestDto();
        given(memberRepository.save(any())).willReturn(new Member(memberSignupDto));
        //when
        memberService.signup(memberSignupDto);
        given(memberRepository.findById(1L)).willReturn(Optional.of(new Member(memberSignupDto)));
        MemberResponseDto memberResponseDto = memberService.findOne(1L);
        //then
        assertThat(memberResponseDto.getLoginId()).isEqualTo(memberSignupDto.getLoginId());

        verify(memberRepository, times(1)).save(any());
        verify(memberRepository, times(1)).findById(1L);

    }

    @Test
    @DisplayName("회원수정 테스트")
    void updateTest() {
        //given
        Member member = MemberFactory.createMember();
        given(memberRepository.findById(1L)).willReturn(Optional.ofNullable(member));
        //when
        MemberUpdateDto updateDto = MemberUpdateDto.builder()
                .password("password수정")
                .address(new Address("zipcode수정", "detailAddress수정"))
                .email("email수정")
                .phone("phone수정")
                .build();
        memberService.update(1L, updateDto);
        //then
        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getLoginId()).isEqualTo("loginId");
        assertThat(member.getPassword()).isEqualTo("password수정");
        assertThat(member.getName()).isEqualTo("name");
        assertThat(member.getAddress().getZipcode()).isEqualTo("zipcode수정");
        assertThat(member.getAddress().getDetailAddress()).isEqualTo("detailAddress수정");
        assertThat(member.getEmail()).isEqualTo("email수정");
        assertThat(member.getPhone()).isEqualTo("phone수정");

    }

    @Test
    @DisplayName("회원삭제 테스트")
    void deleteTest() {
        Member member = MemberFactory.createMember();
        memberRepository.save(member);
        //given
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        //when
        memberService.delete(member.getId());
        //then
        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

}