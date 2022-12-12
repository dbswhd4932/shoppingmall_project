package com.project.shop.member.service;

import com.project.shop.member.controller.request.KakaoLoginRequest;
import com.project.shop.member.controller.request.NoSocialLoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;

import java.util.List;

public interface MemberService {

    // 회원생성
    void memberSignup(MemberSignupRequest memberSignupRequest);

    // 회원 중복체크
    void loginIdDuplicateCheck(String loginId);

    // 일반 로그인
    void noSocialLogin(NoSocialLoginRequest noSocialLoginRequest);

    // 카카오 로그인
    void kakaoLogin(KakaoLoginRequest kakaoLoginRequest);

    // 회원 1명 조회
    MemberResponse memberFindOne(Long memberId);

    // 회원 전체 조회
    List<MemberResponse> memberFindAll();

    // 회원 수정
    void memberEdit(Long memberId, MemberEditRequest memberEditRequest);

    // 회원 삭제
    void memberDelete(Long memberId);


}
