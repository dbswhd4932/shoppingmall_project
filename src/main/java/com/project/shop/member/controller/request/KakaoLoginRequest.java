package com.project.shop.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 *  카카오 로그인에서 동의항목은 닉네임(LoginId) 와 카카오이메일(Email)
 *  해당 정보만 받으며, 나머지는 ""(빈값) 으로 채워진다.
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoLoginRequest {

    private String kakaoLoginId;
    private String kakaoEmail;
}
