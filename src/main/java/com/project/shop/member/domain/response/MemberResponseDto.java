package com.project.shop.member.domain.response;

import com.project.shop.member.domain.entity.Address;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.entity.MemberRole;
import com.project.shop.member.domain.entity.RoleType;
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
    private List<MemberRole> roleType;
    private String email;
    private String phone;

    public MemberResponseDto(Member member){
        this.loginId = member.getLoginId();
        this.password = member.getPassword();
        this.name = member.getName();
        this.address = member.getAddress();
        this.roleType = member.getRoles();
        this.email = member.getEmail();
        this.phone = member.getPhone();
    }
}
