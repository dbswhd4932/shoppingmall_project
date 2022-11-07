package com.project.shop.member.domain.response;

import com.project.shop.member.domain.entity.Address;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.entity.Role;
import lombok.AccessLevel;
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
    private List<Role> roles;
    private String email;
    private String phone;

    public MemberResponseDto(Member member){
        this.loginId = member.getLoginId();
        this.password = member.getPassword();
        this.name = member.getName();
        this.address = member.getAddress();
        this.roles = member.getRoles();
        this.email = member.getEmail();
        this.phone = member.getPhone();
    }
}
