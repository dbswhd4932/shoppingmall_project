package com.project.shop.global.config.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.controller.request.LoginRequest;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {

    private static final String AUTHORITIES_KEY  = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final String MEMBER_ID_CLAIM_KEY = "memberId";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;        // 1일

    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public JwtTokenDto generateToken(LoginRequest loginRequest) throws JsonProcessingException {

        long nowTime = new Date().getTime();
        Member member = memberRepository.findByLoginId(loginRequest.getLoginId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        List<String> roles = member.getRoles().stream().map(x -> x.getRoleType().toString()).toList();

        String data = objectMapper.writeValueAsString(roles);

        Date accessTokenExpiresIn = new Date(nowTime + ACCESS_TOKEN_EXPIRE_TIME); // 1일
        String accessToken = Jwts.builder()
                .setSubject(loginRequest.getLoginId())              //"sub":"소셜닉네임"
                .claim(MEMBER_ID_CLAIM_KEY, member.getId())         //"memberId":"1"
                .claim(AUTHORITIES_KEY, data)                       //  "auth": "[\"ROLE_USER\",\"ROLE_SELLER\"]",
                .claim("LOGIN_TYPE", member.getLoginType()) // "LOGIN_TYPE":"KAKAO"
                .setExpiration(accessTokenExpiresIn)                //"exp":"12345678"
                .signWith(key)
                .compact();

        return JwtTokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    //JWT 토큰을 복호화해서 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) throws JsonProcessingException {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) { // "auth"
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        List<String> data = objectMapper.readValue(claims.get(AUTHORITIES_KEY).toString(), List.class) ;
        List<SimpleGrantedAuthority> authorities = data
                .stream().map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);

    }

    // 토큰정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다. ");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalStateException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 복호화 메서드 따로 생성
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
