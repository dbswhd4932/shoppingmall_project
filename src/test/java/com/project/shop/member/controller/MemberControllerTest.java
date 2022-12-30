package com.project.shop.member.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.factory.MemberFactory;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.repository.RoleRepository;
import com.project.shop.member.service.Impl.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.project.shop.global.error.ErrorCode.DUPLICATED_LOGIN_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .alwaysDo(print())
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("회원가입 - 정상적으로 가입 완료")
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
    @DisplayName("회원 가입 중복체크 - 중복 아이디가 존재해서 예외발생")
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
    @DisplayName("회원 가입 중복체크 - 중복 아이디 없음")
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
    @WithMockUser(roles = "USER")
    @DisplayName("내 정보 가져오기")
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
    @DisplayName("회원 수정")
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
    @DisplayName("회원 탈퇴")
    void memberDelete() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);

        //when
        mockMvc.perform(delete("/api/members")
                        .with(user("loginId").roles("USER")))
                .andExpect(status().isNoContent());

        //then
    }
}