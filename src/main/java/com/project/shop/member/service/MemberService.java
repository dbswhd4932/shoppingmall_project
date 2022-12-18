package com.project.shop.member.service;

import com.project.shop.member.controller.request.KakaoLoginRequest;
import com.project.shop.member.controller.request.NoSocialLoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.jwt.JwtTokenDto;

public interface MemberService {

    // 내정보찾기
    MemberResponse findMemberInfoById(Long memberId);

    // 회원생성
    void memberSignup(MemberSignupRequest memberSignupRequest);

    // 회원 중복체크
    void loginIdDuplicateCheck(String loginId);

    // 일반 로그인
    JwtTokenDto login(NoSocialLoginRequest noSocialLoginRequest);

    // 카카오 로그인
    JwtTokenDto kakaoLogin(KakaoLoginRequest kakaoLoginRequest);

    // 회원 1명 조회
    MemberResponse memberFindByMemberId(Long memberId);

    // 회원 수정
    void memberEdit(Long memberId, MemberEditRequest memberEditRequest);

    // 회원 삭제
    void memberDelete(Long memberId);


}
