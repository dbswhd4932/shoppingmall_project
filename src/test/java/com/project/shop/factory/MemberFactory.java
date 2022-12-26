package com.project.shop.factory;

import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.domain.LoginType;
import com.project.shop.member.domain.Member;
import com.project.shop.member.domain.Role;
import com.project.shop.member.domain.RoleType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

public class MemberFactory {

    PasswordEncoder passwordEncoder;

    public MemberFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Member createMember() {
        Member member = Member.builder()
                .id(1L)
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .name("name")
                .zipcode("123-123")
                .detailAddress("seoul")
                .email("user@test.com")
                .phone("010-1111-1111")
                .roles(List.of(Role.builder().roleType(RoleType.ROLE_USER).build(),
                        Role.builder().roleType(RoleType.ROLE_SELLER).build()))
                .loginType(LoginType.NO_SOCIAL)
                .build();

        return member;
    }

    public MemberSignupRequest createSignupRequestDto() {
        MemberSignupRequest memberSignupRequest = MemberSignupRequest.builder()
                .loginId("loginId")
                .password(passwordEncoder.encode("1234"))
                .name("name")
                .zipcode("123-123")
                .detailAddress("seoul")
                .email("user@test.com")
                .phone("010-1111-1111")
                .roles(List.of(RoleType.ROLE_USER))
                .build();

        return memberSignupRequest;
    }
}
