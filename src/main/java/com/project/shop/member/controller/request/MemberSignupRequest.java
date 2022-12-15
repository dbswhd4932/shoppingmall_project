package com.project.shop.member.controller.request;

import com.project.shop.member.domain.LoginType;
import com.project.shop.member.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSignupRequest {

    @NotNull(message = "로그인 아이디를 입력하세요.")
    @Size(min = 4, message = "로그인 아이디는 4글자 이상이어야 합니다.")
    private String loginId;         // 로그인아이디

    @NotNull(message = "비밀번호를 입력하세요.")
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

    @Enumerated(EnumType.STRING)
    private LoginType loginType;    //로그인 타입

    @Enumerated(EnumType.STRING)
    private Role roles;    // 권한타입

}


