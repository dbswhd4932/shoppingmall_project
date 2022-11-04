package com.project.shop.member.service;

import com.project.shop.factory.MemberFactory;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.MemberSignupDto;
import com.project.shop.member.domain.response.MemberResponse;
import com.project.shop.member.repository.MemberRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
        //when
        when(memberRepository.save(any())).thenReturn(new Member(memberSignupDto));
        //then
        memberService.signup(memberSignupDto);
    }

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
        when(memberRepository.findAll()).thenReturn(list.stream().map(signupRequestDto ->
                new Member(signupRequestDto)).collect(Collectors.toList()));
        //then
        assertThat(memberRepository.findAll().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("회원단건조회 테스트")
    void findOneTest() {
        //given
        MemberSignupDto memberSignupDto = MemberFactory.createSignupRequestDto();
        when(memberRepository.save(any())).thenReturn(new Member(memberSignupDto));

        //when
        memberService.signup(memberSignupDto);
        when(memberRepository.findById(1L)).thenReturn(Optional.of(new Member(memberSignupDto)));

        MemberResponse memberResponse = memberService.findOne(1L);

        //then
        assertThat(memberResponse.getLoginId()).isEqualTo(memberSignupDto.getLoginId());
    }
}