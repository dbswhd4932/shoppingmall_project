package com.project.shop.member.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.controller.request.*;
import com.project.shop.member.domain.LoginType;
import com.project.shop.member.domain.Member;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.jwt.TokenProvider;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 회원생성
    @Override
    public void memberSignup(MemberSignupRequest memberSignupRequest) {
        if (memberRepository.findByLoginId(memberSignupRequest.getLoginId()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
        }

        Member member = Member.create(memberSignupRequest);
//        member.setEncryptedPwd(passwordEncoder.encode(memberSignupRequest.getPassword()));
        memberRepository.save(member);
    }

    // 회원가입 중복체크
    @Override
    public void loginIdDuplicateCheck(String loginId) {
        if (memberRepository.findByLoginId(loginId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
        }
    }

    // todo 일반 로그인
    @Override
    @Transactional
    public JwtTokenDto noSocialLogin(LoginRequest loginRequest) {

        memberRepository.findByLoginIdAndPassword(loginRequest.getLoginId(), loginRequest.getPassword())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHECK_LOGINID_OR_PASSWORD));

        // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
                (loginRequest.getLoginId(), loginRequest.getPassword());

        // 2. 실제 검증 ( 사용자 비밀번호 체크) 이루어지는 부분
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtTokenDto jwtTokenDto = tokenProvider.generateToken(authentication);

        return jwtTokenDto;
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

    // 회원 1명 조회
    @Transactional(readOnly = true)
    @Override
    public MemberResponse memberFindOne(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        return new MemberResponse().toResponse(member);
    }

    // 회원 전체 조회
    @Transactional(readOnly = true)
    @Override
    public List<MemberResponse> memberFindAll() {
        List<MemberResponse> responseList = memberRepository.findAll()
                .stream().map(member -> new MemberResponse().toResponse(member)).collect(Collectors.toList());
        return responseList;
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
