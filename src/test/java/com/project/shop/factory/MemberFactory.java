package com.project.shop.factory;

import com.project.shop.member.domain.entity.Address;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.MemberSignupDto;

public class MemberFactory {

    public static Member createMember() {
        Member member = Member.builder()
                .id(1L)
                .loginId("loginId")
                .password("1234")
                .name("name")
                .address(new Address("zipcode","detailsAddress"))
                .email("user@test.com")
                .phone("010-1111-1111")
                .build();

        return member;
    }

    public static MemberSignupDto createSignupRequestDto() {
        MemberSignupDto memberSignupDto = MemberSignupDto.builder()
                .loginId("loginId")
                .password("1234")
                .name("name")
                .address(new Address("zipcode","detailsAddress"))
                .email("user@test.com")
                .phone("010-1111-1111")
                .build();

        return memberSignupDto;
    }
}
