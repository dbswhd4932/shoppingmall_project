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

    @NotNull(message = "비밀번호를 입력하세요.")
    private String password;        //비밀번호

    @NotNull(message = "우편번호를 입력하세요.")
    private String zipcode;         //우편번호

    @NotNull(message = "상세주소를 입력하세요.")
    private String detailAddress;   //상세주소

    @NotNull(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;           //이메일

    @NotNull(message = "전화번호를 입력하세요.")
    private String phone;           //핸드폰번호
}
