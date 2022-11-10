package com.project.shop.member.domain.request;

import com.project.shop.member.domain.entity.Role;
import lombok.Getter;


@Getter
public class MemberSignupRequest {

    private String loginId;         //회원ID
    private String password;        //비밀번호
    private String name;            //이름
    private String zipcode;         //우편번호
    private String detailAddress;   //상세주소
    private String email;           //이메일
    private String phone;           //핸드폰번호
    private Role role;              //권한 초기 : USER
}
