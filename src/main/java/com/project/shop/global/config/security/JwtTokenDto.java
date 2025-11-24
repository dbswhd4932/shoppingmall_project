package com.project.shop.global.config.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JwtTokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;        // 추가: Refresh Token
    private Long accessTokenExpiresIn;
    private Long refreshTokenExpiresIn; // 추가: Refresh Token 만료 시간
}
