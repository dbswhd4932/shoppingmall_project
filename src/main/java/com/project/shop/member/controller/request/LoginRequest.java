package com.project.shop.member.controller.request;

import com.project.shop.member.domain.LoginType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @Column(nullable = false)
    private String loginId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LoginType loginType; // NO_SOCIAL , KAKAO

    @Column
    private String email;       // kakao 동의 항목 (email)

}
