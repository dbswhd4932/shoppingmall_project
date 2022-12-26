package com.project.shop.member.service.Impl;

import com.project.shop.factory.MemberFactory;
import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.controller.request.LoginRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.domain.LoginType;
import com.project.shop.member.domain.Member;
import com.project.shop.member.jwt.JwtTokenDto;
import com.project.shop.member.jwt.RefreshTokenRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.repository.RoleRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @InjectMocks
    MemberServiceImpl memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 생성")
    void memberSignupTest() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        MemberSignupRequest signupRequestDto = memberFactory.createSignupRequestDto();
        //when
        memberService.memberSignup(signupRequestDto);
        //then
        verify(memberRepository).save(any());

    }

    @Test
    @DisplayName("회원가입 중복체크")
    void loginIdDuplicateCheck() {
        //given
        given(memberRepository.findByLoginId(any()).isPresent())
                .willThrow(new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID));

        //when
        //then
        assertThrows(BusinessException.class,
                () -> {
                    memberService.loginIdDuplicateCheck("loginId");
                });
    }

    @Test
    @DisplayName("로그인")
    void login() {
        //given
        LoginRequest loginRequest = new LoginRequest("loginId", "1234", LoginType.NO_SOCIAL, "test@test.com");

        //when
        JwtTokenDto tokenDto = memberService.login(loginRequest);

        //then
        Assertions.assertThat(tokenDto).isNotNull();

    }

    @Test
    @DisplayName("로그인 실패")
    @Disabled
    void loginFail() {
        //given
        LoginRequest loginRequest = new LoginRequest("loginId", "1234", LoginType.NO_SOCIAL, "test@test.com");
        Member member = Member.builder().loginId("loginId").password("123").build();
        given(memberRepository.save(member)).willReturn(member);
        //when

        //then

    }
}