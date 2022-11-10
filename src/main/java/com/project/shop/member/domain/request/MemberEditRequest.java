package com.project.shop.member.domain.request;

import lombok.Getter;

@Getter
public class MemberEditRequest {

    private String password;        //비밀번호
    private String zipcode;         //우편번호
    private String detailAddress;   //상세주소
    private String email;           //이메일
    private String phone;           //핸드폰번호
}
