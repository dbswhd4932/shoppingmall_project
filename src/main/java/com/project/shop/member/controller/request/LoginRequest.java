package com.project.shop.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    private String loginId;
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(loginId, password);
    }

}
