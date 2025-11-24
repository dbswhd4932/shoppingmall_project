package com.project.shop.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * 토큰 재발급 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenReissueRequest {

    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;
}
