package com.project.shop.member.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.controller.request.KakaoLoginRequest;
import com.project.shop.member.controller.request.NoSocialLoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.domain.Member;
import com.project.shop.member.domain.Role;
import com.project.shop.member.domain.RoleType;
import com.project.shop.member.jwt.JwtTokenDto;
import com.project.shop.member.jwt.RefreshToken;
import com.project.shop.member.jwt.RefreshTokenRepository;
import com.project.shop.member.jwt.TokenProvider;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.repository.RoleRepository;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;

    // 회원생성
    @Override
    public void memberSignup(MemberSignupRequest memberSignupRequest) {
        if (memberRepository.findByLoginId(memberSignupRequest.getLoginId()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
        }

        Member member = Member.create(memberSignupRequest, passwordEncoder);
        memberRepository.save(member);

        for (RoleType role : memberSignupRequest.getRoles()) {
            Role saveRole = Role.builder()
                    .roleType(role)
                    .member(member)
                    .build();

            roleRepository.save(saveRole);
        }
    }

    // 회원가입 중복체크
    @Override
    public void loginIdDuplicateCheck(String loginId) {
        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
        }
    }

    // 일반 로그인
    @Override              // String memberId, String password
    public JwtTokenDto login(NoSocialLoginRequest noSocialLoginRequest) {
        // 1. 로그인 Id / Pw 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(noSocialLoginRequest.getLoginId(), noSocialLoginRequest.getPassword());
        // 2. 실제 검증 (사용자 비밀번호 체크)
        // authenticate 메서드가 실행 될때 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 3. 인증기반으로 jwt 토큰생성
        JwtTokenDto tokenDto = tokenProvider.generateToken(authentication);
        // 4. 리프레시 토큰 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        // 5. 토큰 리턴
        return tokenDto;
    }

    // todo 카카오 로그인
    @Override
    public void kakaoLogin(KakaoLoginRequest kakaoLoginRequest) {
        Member member = Member.kakaoCreate(kakaoLoginRequest);
        memberRepository.save(member);

        Role role = Role.builder()
                .member(member)
                .roleType(RoleType.ROLE_USER)
                .build();
        roleRepository.save(role);
    }

    @Override
    // 카카오 로그인 토큰 얻기
    public JwtTokenDto kakaoGetToken(KakaoLoginRequest kakaoLoginRequest) {

        return null;
    }

    // 내 정보 조회
    // todo 내 정보 조회 권한 부분 질문
    @Override
    public MemberResponse findByDetailMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        MemberResponse memberResponse = new MemberResponse().toResponse(member);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        memberResponse.setRoles((List<GrantedAuthority>) authorities);

        return memberResponse;

    }

    // 회원 수정
    @Override
    public void memberEdit(Long memberId, MemberEditRequest memberEditRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        member.edit(memberEditRequest);
    }

    // 회원 탈퇴
    @Override
    public void memberDelete(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        member.setDeletedAt();
    }
}
