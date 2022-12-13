package com.project.shop.member.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.controller.request.*;
import com.project.shop.member.domain.LoginType;
import com.project.shop.member.domain.Member;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.jwt.RefreshToken;
import com.project.shop.member.jwt.RefreshTokenRepository;
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
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원생성
    @Override
    public void memberSignup(MemberSignupRequest memberSignupRequest) {
        if (memberRepository.findByLoginId(memberSignupRequest.getLoginId()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
        }

        Member member = Member.create(memberSignupRequest, passwordEncoder);
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
    public JwtTokenDto login(LoginRequest loginRequest) {

        memberRepository.findByLoginId(loginRequest.getLoginId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CHECK_LOGINID_OR_PASSWORD));

        UsernamePasswordAuthenticationToken authenticationToken = loginRequest.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        JwtTokenDto tokenDto = tokenProvider.generateToken(authentication);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;

    }

    @Transactional
    public JwtTokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        JwtTokenDto tokenDto = tokenProvider.generateToken(authentication);

        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        // 토큰 발급
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
