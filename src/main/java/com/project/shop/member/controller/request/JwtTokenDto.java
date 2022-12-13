package com.project.shop.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JwtTokenDto {
    private String grantType; // JWT 대한 인증타입 , Bearer 를 사용한다.
    private String accessToken;
    private String refreshToken;
}
