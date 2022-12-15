package com.project.shop.member.controller.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.shop.member.domain.Member;
import com.project.shop.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embedded;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private String loginId;         //회원 ID
    private String password;        //비밀번호
    private String name;            //이름
    private String zipcode;         //우편번호
    private String detailAddress;   //상세주소
    private String email;           //이메일
    private String phone;           //핸드폰번호
    private List<String> roles;    //권한

    // Member -> MemberResponse 변환
    public MemberResponse toResponse(Member m) {
        MemberResponse memberResponse = MemberResponse.builder()
                .loginId(m.getLoginId())
                .password(m.getPassword())
                .name(m.getName())
                .zipcode(m.getZipcode())
                .detailAddress(m.getDetailAddress())
                .email(m.getEmail())
                .phone(m.getPhone())
                .roles(m.getRoles())
                .build();

//        memberResponse.setRoles(m.getRoles());
        return memberResponse;
    }

    public void setRoles(List<String> roles) {
        roles.add(roles.stream().map(role -> roles.add(role)).toString());
    }
}
