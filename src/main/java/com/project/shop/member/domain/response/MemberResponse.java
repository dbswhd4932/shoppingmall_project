package com.project.shop.member.domain.response;

import com.project.shop.member.domain.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class MemberResponse {

    private Long id;

    private String loginId;

    private String password;

    private String name;

    private String zipcode;

    private String detailAddress;

    private String email;

    private String phone;

    public MemberResponse(Member member){
        this.id = member.getId();
        this.loginId = member.getLoginId();
        this.password = member.getPassword();
        this.name = member.getName();
        this.zipcode = member.getZipcode();
        this.detailAddress = member.getDetailAddress();
        this.email = member.getEmail();
        this.phone = member.getPhone();
    }
}
