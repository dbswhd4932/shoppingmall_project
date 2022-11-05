package com.project.shop.member.domain.response;

import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class MemberResponseDto {

    private String loginId;

    private String password;

    private String name;

    private String zipcode;

    private List<Role> roles;

    private String detailAddress;

    private String email;

    private String phone;

    public MemberResponseDto(Member member){
        this.loginId = member.getLoginId();
        this.password = member.getPassword();
        this.name = member.getName();
        this.zipcode = member.getZipcode();
        this.roles = member.getRoles();
        this.detailAddress = member.getDetailAddress();
        this.email = member.getEmail();
        this.phone = member.getPhone();
    }
}
