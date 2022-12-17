package com.project.shop.member.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.controller.request.KakaoLoginRequest;
import com.project.shop.member.controller.request.LoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.domain.LoginType;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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

    // todo 일반 로그인
    @Override              // String memberId, String password
    public JwtTokenDto login(LoginRequest loginRequest) {

        memberRepository.findByLoginId(loginRequest.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHECK_LOGINID_OR_PASSWORD));

        // 1. 로그인 Id / Pw 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginRequest.toAuthentication();

        // 2. 실제 검증 (사용자 비밀번호 체크)
//         authenticate 메서드가 실행 될때 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

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
        Member member = Member.builder()
                .loginId(kakaoLoginRequest.getKakaoLoginId())
                .email(kakaoLoginRequest.getKakaoEmail())
                .loginType(LoginType.KAKAO)
                .build();

        memberRepository.save(member);
    }

    // 회원 조회 (회원 ID)
    @Override
    public MemberResponse findMemberInfoById(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        return new MemberResponse().toResponse(member);
    }

    // 회원 1명 조회
    @Transactional(readOnly = true)
    @Override
    public MemberResponse memberFindByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        return new MemberResponse().toResponse(member);
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
