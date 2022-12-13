package com.project.shop.member.jwt;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;

// 권한이나 인증이 필요한 특정 주소를 요청했을 때, 해당 필터를 타게 된다.
// 권한이나 인증이 필요하지 않으면 거치지 않는다.
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberRepository memberRepository;
    @Value("${jwt.secret}")
    private String SECRET_KEY;


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
    }

    // 인증이나 권한이 필요한 주소요청이 있으면 해당 필터를 거친다.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        log.info("1.인증 or 권한 필터 진행");
        // 1. 권한이나 인증이 필요한 요청이 전달된다.
        log.info("권한 or 인증이 필요한 요청 전달");
        String jwtHeader = request.getHeader("Authorization"); // 헤더에 있는 Authorization 꺼냄
        log.info("jwtHeader = " + jwtHeader);
        // 2. Header 확인
        log.info("2.검증확인");
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }
        // 3. JWT 토큰을 검증해서 정상적인 사용자인지 확인
        log.info("3. JWT 토큰을 검증해서 정상적인 사용자인지 확인");
        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");
        String loginId = null;
        try {
            loginId = Jwts.parserBuilder()
                    .setSigningKey(change(SECRET_KEY))
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody().get("loginId")
                    .toString();

            log.info("loginId = " + loginId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("토큰 형식 오류입니다!!!");
        }

        if (loginId != null) {
            // 정상 서명
            log.info("정상 서명 확인");
            Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                    () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
            PrincipalDetails principalDetails = new PrincipalDetails(member);

            log.info("Authentication 객체 생성");
            // 이미 검증되었으므로 비밀번호는 필요없다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            // 강제로 시큐리티 세션에 접근해서 Authentication 객체 저장
            log.info("시큐리티 세션 접근해서 Authentication 객체 저장");
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        }
    }

    public Key change(String secretKey) {
        byte[] bytes = secretKey.getBytes();
        SecretKey key = Keys.hmacShaKeyFor(bytes);
        return key;
    }
}
