package com.project.shop.member.service.Impl;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Image;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ImageRepository;
import com.project.shop.member.controller.request.LoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.domain.*;
import com.project.shop.member.jwt.JwtTokenDto;
import com.project.shop.member.jwt.TokenProvider;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("회원 서비스 테스트")
class MemberServiceImplTest {

    @InjectMocks
    MemberServiceImpl memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    GoodsRepository goodsRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    ImageRepository imageRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    AuthenticationManagerBuilder authenticationManagerBuilder;

    @Mock
    TokenProvider tokenProvider;



    @Mock
    PasswordEncoder passwordEncoder;


    @BeforeEach()
    void beforeEach() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TestingAuthenticationToken mockAuthentication = new TestingAuthenticationToken("loginId", "1234");
        context.setAuthentication(mockAuthentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("회원 생성")
    void memberSignupTest() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Role role = new Role(RoleType.ROLE_USER, member);
        MemberSignupRequest signupRequestDto = memberFactory.createSignupRequestDto();
        given(roleRepository.save(any())).willReturn(role);

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
    @DisplayName("카카오 로그인")
    void SocialLogin() {
        //given
        LoginRequest loginRequest = new LoginRequest("loginId", "1234", LoginType.KAKAO, "test@test.com");
        JwtTokenDto tokenDto = new JwtTokenDto("jwt", "accessToken",  1234L);
        given(tokenProvider.generateTokenNoSecurity(loginRequest)).willReturn(tokenDto);

        //when
        memberService.login(loginRequest);

        //then
        assertThat(tokenDto.getGrantType()).isEqualTo("jwt");
    }

    @Test
    @DisplayName("일반 로그인")
    @WithMockUser
    void noSocialLogin() {
        //given
        LoginRequest loginRequest = new LoginRequest("loginId", "1234", LoginType.NO_SOCIAL, "test@test.com");

        //when
        memberService.login(loginRequest);

        //then
    }

    @Test
    @DisplayName("내 정보 조회")
    void findByDetailMyInfo() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));

        //when
        MemberResponse memberResponse = memberService.findByDetailMyInfo();

        //then
        assertThat(memberResponse.getLoginId()).isEqualTo("loginId");
    }

    @Test
    @DisplayName("회원 수정")
    void memberEdit() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));

        MemberEditRequest memberEditRequest
                = new MemberEditRequest("4321", "zipcode", "detailAddress",
                "test@test.com", "010-1234-1234");

        //when
        memberService.memberEdit(memberEditRequest);

        //then
        assertThat(member.getPhone()).isEqualTo("010-1234-1234");

    }

    @Test
    @DisplayName("회원 탈퇴")
    void memberDelete() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Image image = new Image("https://goods-image.s3.ap-northeast-2.amazonaws.com/9a6347ed-56b0-47de-b194-6f865aa4f5ca.png", goods);
        Cart cart = new Cart(1L, member, goods.getId(), 10, 10000, 1L);
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(goodsRepository.findAllByMemberId(member.getId())).willReturn(List.of(goods));
        given(cartRepository.findByMemberId(member.getId())).willReturn(List.of(cart));
        given(imageRepository.findByGoodsId(goods.getId())).willReturn(List.of(image));

        //when
        memberService.memberDelete();

        //then
        verify(goodsRepository).deleteById(goods.getId());
    }
}