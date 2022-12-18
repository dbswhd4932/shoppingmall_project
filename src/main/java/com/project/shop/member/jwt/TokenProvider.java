package com.project.shop.member.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY  = "auth";
    private static final String BEARER_TYPE = "Bearer";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;            // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;      // 1일

    private final Key key;

    //생성자
    public TokenProvider(@Value("${jwt.secret}") String secretKey) {
        // base64를 byte[] 로 변환
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // byte[] 로 key 생성
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 유저 정보를 가지고 AccessToken , RefreshToken을 생성하는 메서드
    public JwtTokenDto generateToken(Authentication authentication) {
        //권한 가져오기
        String collect = authentication.getAuthorities().stream()
                .map(String::valueOf)
                .collect(Collectors.joining());

        /**
         *  권한이 2개 이상일 경우 EX) [ROLE_ADMIN, ROLE_USER]
         *  JWT auth 는 문자열 그대로  [ROLE_ADMIN, ROLE_USER] 로 들어가기 때문에, ROLE_ADMIN, ROLE_USER 로 커스텀이 필요합니다.
         *  대괄호 [ ] 를 제거한 후 토큰 생성 시 auth 값에 추가해줍니다.
         */
        String authorization = collect.substring(1, collect.length() - 1);

        long nowTime = new Date().getTime();

        //AccessToken 생성
        Date accessTokenExpiresIn = new Date(nowTime + ACCESS_TOKEN_EXPIRE_TIME); // 30분 60 * 30 * 1000

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())       //"sub":"name"
                .claim(AUTHORITIES_KEY, authorization)     //"auth":"ROLE_USER"
                .setExpiration(accessTokenExpiresIn)       //"exp":"12345678"
                .signWith(key, SignatureAlgorithm.HS512)   //"alg":"HS512"
                .compact();

        // RefreshToken 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(nowTime + REFRESH_TOKEN_EXPIRE_TIME)) // 1일 24 * 60 * 60 * 1000
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        return JwtTokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .build();
    }

    //JWT 토큰을 복호화해서 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) { // "auth"
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        List<SimpleGrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(", "))
                .map(role -> new SimpleGrantedAuthority(role))
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
