package com.project.shop.member.domain.request;

import com.project.shop.member.domain.entity.Address;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignupDto {

    private String loginId;
    private String password;
    private String name;
    private Address address;
    private String email;
    private String phone;

    @Builder
    public MemberSignupDto(String loginId, String password, String name, Address address, String email, String phone) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
    }
}
