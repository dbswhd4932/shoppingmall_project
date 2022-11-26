package com.project.shop.factory;

import com.project.shop.member.domain.Member;
import com.project.shop.member.controller.request.MemberSignupRequest;

public class MemberFactory {

    public static Member createMember() {
        Member member = Member.builder()
                .loginId("loginId")
                .password("1234")
                .name("name")
                .zipcode("123-123")
                .detailAddress("seoul")
                .email("user@test.com")
                .phone("010-1111-1111")
                .build();

        return member;
    }

    public static MemberSignupRequest createSignupRequestDto() {
        MemberSignupRequest memberSignupRequest = MemberSignupRequest.builder()
                .password("1234")
                .name("name")
                .zipcode("123-123")
                .detailAddress("seoul")
                .email("user@test.com")
                .phone("010-1111-1111")
                .build();

        return memberSignupRequest;
    }
}
