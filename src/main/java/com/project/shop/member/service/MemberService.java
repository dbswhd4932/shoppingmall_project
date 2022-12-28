package com.project.shop.member.service;

import com.project.shop.member.controller.request.LoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.global.config.security.JwtTokenDto;

public interface MemberService {

    // 내정보찾기
    MemberResponse findByDetailMyInfo();

    // 회원생성
    void memberSignup(MemberSignupRequest memberSignupRequest);

    // 회원 중복체크
    void loginIdDuplicateCheck(String loginId);

    // 일반 로그인
    JwtTokenDto login(LoginRequest noSocialLoginRequest);

    // 회원 수정
    void memberEdit(MemberEditRequest memberEditRequest);

    // 회원 삭제
    void memberDelete();


}
