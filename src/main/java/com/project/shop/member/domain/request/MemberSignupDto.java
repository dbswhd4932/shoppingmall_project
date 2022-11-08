package com.project.shop.member.domain.request;

import com.project.shop.member.domain.entity.Address;
import com.project.shop.member.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class MemberSignupDto {

    private String loginId;
    private String password;
    private String name;
    private Address address;
    private String email;
    private String phone;
    private List<Role> role;

}
