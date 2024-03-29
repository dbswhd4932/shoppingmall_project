package com.project.shop.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    // 일반 로그인                               //자바 객체 -> JSON 문자열
    JwtTokenDto login(LoginRequest loginRequest) throws JsonProcessingException;

    // 회원 수정
    void memberEdit(MemberEditRequest memberEditRequest);

    // 회원 삭제
    void memberDelete();

    // 로그인 히스토리 삭제 스케줄러
    void schedulerLoginHistoryDeleteCron();

}
