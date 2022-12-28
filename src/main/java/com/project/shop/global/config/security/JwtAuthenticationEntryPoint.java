package com.project.shop.global.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, IOException {
        // 유효한 자격증명을 제공하지 않고 접근하려 할때 401
        log.info("아이디 또는 비밀번호를 확인하세요.");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}