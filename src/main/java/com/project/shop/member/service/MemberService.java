package com.project.shop.member.service;

import com.project.shop.member.domain.request.MemberEditRequest;
import com.project.shop.member.domain.request.MemberSignupRequest;
import com.project.shop.member.domain.response.MemberResponse;

import java.util.List;

public interface MemberService {

    // 회원생성
    void memberSignup(MemberSignupRequest memberSignupRequest);

    // 회원 1명 조회
    MemberResponse memberFindOne(Long memberId);

    // 회원 전체 조회
    List<MemberResponse> memberFindAll();

    // 회원 수정
    void memberEdit(Long memberId, MemberEditRequest memberEditRequest);

    // 회원 삭제
    void memberDelete(Long memberId);


}
