package com.project.shop.member.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.shop.member.domain.Member;
import com.project.shop.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private String loginId;         //회원ID
    private String password;        //비밀번호
    private String name;            //이름
    private String zipcode;         //우편번호
    private String detailAddress;   //상세주소
    private String email;           //이메일
    private String phone;           //핸드폰번호
    private Role role;              //권한

    // Member -> MemberResponse 변환
    public MemberResponse toResponse(Member m) {
        return new MemberResponse(
                m.getLoginId(),
                m.getPassword(),
                m.getName(),
                m.getZipcode(),
                m.getDetailAddress(),
                m.getEmail(),
                m.getPhone(),
                m.getRole());
    }
}
