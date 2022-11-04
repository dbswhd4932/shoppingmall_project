package com.project.shop.member.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateDto {

    private String password;
    private String zipcode;
    private String detailAddress;
    private String email;
    private String phone;

}
