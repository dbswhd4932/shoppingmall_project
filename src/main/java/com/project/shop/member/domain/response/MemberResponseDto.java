package com.project.shop.member.domain.response;

import com.project.shop.member.domain.entity.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
public class MemberResponseDto {

    private String loginId;
    private String password;
    private String name;
    private Address address;
    private List<MemberRole> memberRoles;
    private String email;
    private String phone;

    public MemberResponseDto(Member member){
        this.loginId = member.getLoginId();
        this.password = member.getPassword();
        this.name = member.getName();
        this.address = member.getAddress();
        this.memberRoles = member.getMemberRoleList();
        this.email = member.getEmail();
        this.phone = member.getPhone();
    }
}
