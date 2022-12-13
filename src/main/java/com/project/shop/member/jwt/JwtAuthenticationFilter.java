package com.project.shop.member.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shop.member.controller.request.LoginRequest;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Date;

import static org.springframework.security.config.Elements.JWT;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // login 요청을 하면 로그인 시도를 위해서 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        log.info("로그인 시도");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            // 1. username , password 를 받는다.
            log.info("1.username, password 받기");
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            log.info(loginRequest.toString());
            // username, password 를 이용해서 token 발급
            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword());
            log.info(authenticationToken.getPrincipal().toString());
            log.info(authenticationToken.getCredentials().toString());

            // 2. 정상 로그인 여부 검증
            log.info("2.로그인 시도 여부 검증");
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // 3. 로그인 성공
            log.info("3.로그인 성공");
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            log.info("loginId = " + principalDetails.getMember().getLoginId());
            log.info("password = " + principalDetails.getMember().getPassword());

            // 4. Authentication 반환
            return authentication;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 실제 로그인 성공했을 때 어떤 처리를 할지
    // JWT 토큰을 만들어서 요청 사용자에게 response 해준다.
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        log.info("인증완료");
        PrincipalDetails principal = (PrincipalDetails) authResult.getPrincipal();

        String jwt = Jwts.builder()
                .setSubject("jwt토큰")
                .setExpiration(new Date(System.currentTimeMillis() + (60 * 10 * 1000))) // 10분
                .signWith(change(SECRET_KEY), SignatureAlgorithm.HS512)
                .claim("id", principal.getMember().getId())
                .claim("loginId", principal.getMember().getLoginId())
                .compact();

        response.addHeader("Authorization", "Bearer " + jwt);
    }

    public Key change(String secretKey) {
        byte[] bytes = secretKey.getBytes();
        SecretKey key = Keys.hmacShaKeyFor(bytes);
        return key;
    }

}
