package com.project.shop.member.domain.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignupDto {

    private String loginId;
    private String password;
    private String name;
    private String zipcode;
    private String detailAddress;
    private String email;
    private String phone;

    @Builder
    public MemberSignupDto(String loginId, String password, String name, String zipcode, String detailAddress, String email, String phone) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.zipcode = zipcode;
        this.detailAddress = detailAddress;
        this.email = email;
        this.phone = phone;
    }


}
