package com.project.shop.member.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.factory.MemberFactory;
import com.project.shop.global.config.security.JwtTokenDto;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.controller.request.LoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.domain.LoginType;
import com.project.shop.member.domain.Member;
import com.project.shop.member.domain.Role;
import com.project.shop.member.domain.RoleType;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.repository.RoleRepository;
import com.project.shop.member.service.Impl.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.shop.global.error.ErrorCode.DUPLICATED_LOGIN_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ImportAutoConfiguration(WebSecurityConfig.class)
@DisplayName("회원 컨트롤러 통합테스트")
class MemberControllerTest extends ControllerSetting {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    MemberServiceImpl memberService;

    @Test
    @DisplayName("회원가입_성공")
    void memberSignup() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        MemberSignupRequest signupRequestDto = memberFactory.createSignupRequestDto();

        //when
        mockMvc.perform(post("/api/members/signup")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequestDto)))
                .andExpect(status().isCreated());

        //then
        Assertions.assertThat(memberRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("회원아이디중복체크_실패_중복아이디존재")
    void loginIdDuplicateCheckSuccess() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        String loginId = "loginId";

        //when
        mockMvc.perform(post("/api/members/exist")
                        .queryParam("loginId", loginId))
                .andExpect(status().isBadRequest());
        //then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            memberService.loginIdDuplicateCheck(loginId);
        });
        assertThat(exception.getErrorCode()).isEqualTo(DUPLICATED_LOGIN_ID);
    }

    @Test
    @DisplayName("회원아이디중복체크_성공")
    void loginIdDuplicateCheckFail() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        String loginId = "loginId2";

        //when //then
        mockMvc.perform(post("/api/members/exist")
                        .queryParam("loginId", loginId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("로그인_성공_일반로그인")
    void noSocialLogin() throws Exception {
        //given
        Role role = Role.builder().roleType(RoleType.ROLE_USER).build();
        roleRepository.save(role);
        Member member = Member.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .name("name")
                .zipcode("123-123")
                .detailAddress("seoul")
                .email("user@test.com")
                .phone("010-1111-1111")
                .roles(List.of(role))
                .loginType(LoginType.NO_SOCIAL)
                .build();
        memberRepository.save(member);

        LoginRequest loginRequest = LoginRequest.builder()
                .loginType(LoginType.NO_SOCIAL)
                .loginId("loginId")
                .password("1234")
                .build();

        JwtTokenDto tokenDto = memberService.login(loginRequest);

        //when
        mockMvc.perform(post("/api/members/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        //then
        assertThat(tokenDto).isNotNull();

    }

    @Test
    @DisplayName("로그인_성공_카카오로그인")
    void kakaoLogin() throws Exception {
        //given
        LoginRequest loginRequest = LoginRequest.builder()
                .loginType(LoginType.KAKAO)
                .loginId("loginId")
                .password("1234")
                .email("kakao@test.com")
                .build();

        Member member = Member.kakaoCreate(loginRequest, passwordEncoder);
        memberRepository.save(member);

        JwtTokenDto tokenDto = memberService.login(loginRequest);

        //when
        mockMvc.perform(post("/api/members/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

        //then
        assertThat(tokenDto).isNotNull();

    }

    @Test
    @DisplayName("로그인_실패_아이디_비빌번호_오류")
    void noSocialLoginUnAuthorized() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        LoginRequest loginRequest = LoginRequest.builder().loginType(LoginType.NO_SOCIAL)
                .loginId("loginId1234")
                .password("1234")
                .build();

        //when then
        mockMvc.perform(post("/api/members/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("내정보조회_성공")
    void findByDetailMyInfo() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        MemberResponse memberResponse = MemberResponse.toResponse(member);

        //when
        mockMvc.perform(get("/api/members/me")
                        .with(user("loginId").roles("USER")))
                .andExpect(status().isOk());

        //then
        assertThat(memberResponse.getLoginId()).isEqualTo("loginId");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("회원수정_성공")
    void memberEdit() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);

        MemberEditRequest memberEditRequest = MemberEditRequest.builder()
                .password(passwordEncoder.encode("123"))
                .phone("010-7777-7777")
                .email("test@google.com")
                .zipcode("zipcodeEdit")
                .detailAddress("addressEdit")
                .build();

        //when
        mockMvc.perform(put("/api/members")
                        .contentType(APPLICATION_JSON)
                        .with(user("loginId").roles("USER"))
                        .content(objectMapper.writeValueAsString(memberEditRequest)))
                .andExpect(status().isOk());
        //then
        assertThat(memberRepository.findByLoginId("loginId").get().getPhone())
                .isEqualTo("010-7777-7777");
    }

    @Test
    @WithMockUser(roles = {"USER", "SELLER"})
    @DisplayName("회원탈퇴_성공")
    void memberDelete() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        for (Role role : member.getRoles()) {
            roleRepository.save(role);
        }

        memberRepository.save(member);

        //when
        mockMvc.perform(delete("/api/members")
                        .with(user("loginId").roles("USER")))
                .andExpect(status().isNoContent());

        //then
        assertThat(memberRepository.findByLoginId("loginId").get().getDeletedAt()).isNotNull();

    }
}