package com.project.shop.member.service;

import com.project.shop.factory.MemberFactory;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.MemberSignupDto;
import com.project.shop.member.domain.request.MemberUpdateDto;
import com.project.shop.member.domain.response.MemberResponse;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
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
        MemberResponse memberResponse = memberService.findOne(1L);
        //then
        assertThat(memberResponse.getLoginId()).isEqualTo(memberSignupDto.getLoginId());

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
                .zipcode("zipcode수정")
                .detailAddress("detailAddress수정")
                .email("email수정")
                .phone("phone수정")
                .build();
        memberService.update(1L, updateDto);
        //then
        assertThat(member.getId()).isEqualTo(1L);
        assertThat(member.getLoginId()).isEqualTo("loginId");
        assertThat(member.getPassword()).isEqualTo("password수정");
        assertThat(member.getName()).isEqualTo("name");
        assertThat(member.getZipcode()).isEqualTo("zipcode수정");
        assertThat(member.getDetailAddress()).isEqualTo("detailAddress수정");
        assertThat(member.getEmail()).isEqualTo("email수정");
        assertThat(member.getPhone()).isEqualTo("phone수정");

    }

    @Test
    @DisplayName("회원삭제 테스트")
    void deleteTest() {
        Member member = MemberFactory.createMember();
        memberRepository.save(member);
        //given
        given(memberRepository.findById(1L)).willReturn(Optional.of(member));
        //when
        memberService.delete(1L);
        //then
        assertThat(memberRepository.findAll().size()).isEqualTo(0);
    }

}