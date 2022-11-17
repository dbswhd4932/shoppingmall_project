package com.project.shop.member.controller.request;

import com.project.shop.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignupRequest {


    @Min(value = 6, message = "비밀번호는 6글자 이상 입력하세요.")
    private String password;        //비밀번호

    @NotNull(message = "이름을 입력하세요.")
    private String name;            //이름

    @NotNull(message = "우편번호를 입력하세요.")
    private String zipcode;         //우편번호

    @NotNull(message = "상세주소를 입력하세요.")
    private String detailAddress;   //상세주소

    @Email(message = "이메일 형식으로 입력하세요")
    private String email;           //이메일

    @NotNull(message = "전화번호를 입력하세요.")
    private String phone;           //핸드폰번호

    private Role role;              //권한 초기 : USER


}
