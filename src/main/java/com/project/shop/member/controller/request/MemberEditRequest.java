package com.project.shop.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEditRequest {

    private String password;        //비밀번호
    private String zipcode;         //우편번호
    private String detailAddress;   //상세주소
    private String email;           //이메일
    private String phone;           //핸드폰번호
}
