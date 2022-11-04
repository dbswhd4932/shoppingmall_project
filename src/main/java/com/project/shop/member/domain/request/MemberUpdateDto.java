package com.project.shop.member.domain.request;

import lombok.*;

@Getter
@NoArgsConstructor
@ToString
public class MemberUpdateDto {

    private String password;
    private String zipcode;
    private String detailAddress;
    private String email;
    private String phone;

    @Builder
    public MemberUpdateDto(String password, String zipcode, String detailAddress, String email, String phone) {
        this.password = password;
        this.zipcode = zipcode;
        this.detailAddress = detailAddress;
        this.email = email;
        this.phone = phone;
    }
}
