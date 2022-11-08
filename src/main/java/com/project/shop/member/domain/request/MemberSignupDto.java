package com.project.shop.member.domain.request;

import com.project.shop.member.domain.entity.Address;
import com.project.shop.member.domain.entity.MemberRole;
import com.project.shop.member.domain.entity.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemberSignupDto {

    private String loginId;
    private String password;
    private String name;
    private Address address;
    private String email;
    private String phone;
    private List<MemberRole> memberRole;

}
