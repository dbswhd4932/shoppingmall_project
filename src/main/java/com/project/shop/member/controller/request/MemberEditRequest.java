package com.project.shop.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEditRequest {

    private String password;        //비밀번호 (선택)

    @NotNull(message = "이름을 입력하세요.")
    private String name;            //이름

    private String zipcode;         //우편번호

    private String detailAddress;   //상세주소

    @NotNull(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;           //이메일

    private String phone;           //핸드폰번호
}
