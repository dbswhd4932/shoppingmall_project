package com.project.shop.member.service;

import com.project.shop.member.controller.request.KakaoLoginRequest;
import com.project.shop.member.controller.request.NoSocialLoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.jwt.JwtTokenDto;

public interface MemberService {

    // 내정보찾기
    MemberResponse findByDetailMyInfo();

    // 회원생성
    void memberSignup(MemberSignupRequest memberSignupRequest);

    // 회원 중복체크
    void loginIdDuplicateCheck(String loginId);

    // 일반 로그인
    JwtTokenDto login(NoSocialLoginRequest noSocialLoginRequest);

    // 카카오 로그인
    void kakaoLogin(KakaoLoginRequest kakaoLoginRequest);

    // 카카오 토큰 생성
    JwtTokenDto kakaoGetToken(KakaoLoginRequest kakaoLoginRequest);

    // 회원 수정
    void memberEdit(Long memberId, MemberEditRequest memberEditRequest);

    // 회원 삭제
    void memberDelete(Long memberId);


}
