package com.project.shop.member.controller;

import com.project.shop.global.util.SecurityUtil;
import com.project.shop.member.controller.request.*;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.jwt.JwtTokenDto;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    // 내 정보 가져오기
    @GetMapping("/member/me")
    public MemberResponse findMemberInfoById() {
        return memberService.findMemberInfoById(SecurityUtil.getCurrentMemberId());
    }


    // loginId 로 회원찾기
    @GetMapping("/member/{loginId}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse findMemberInfoByLoginId(@PathVariable String logindId) {
        return memberService.findMemberInfoLoginId(logindId);
    }

    //중복체크 먼저 하고나서 -> 회원 가입
    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void memberSignup(@RequestBody @Valid MemberSignupRequest request) {
        memberService.memberSignup(request);
    }

    //로그인 아이디 중복 체크
    @GetMapping("/members/{loginId}/exist")
    @ResponseStatus(HttpStatus.OK)
    public void memberLoginIdDuplicateCheck(@PathVariable("loginId") String loginId) {
        memberService.loginIdDuplicateCheck(loginId);
    }

    //todo 일반 로그인 -> 시큐리티 적용(Token) 필요
    @PostMapping("/members/login")
    @ResponseStatus(HttpStatus.OK)
    public JwtTokenDto login(@RequestBody LoginRequest loginRequest) {
        return memberService.login(loginRequest);
    }


    //todo 소셜 로그인
    @PostMapping("/members/kakaoLogin")
    @ResponseStatus(HttpStatus.OK)
    public void kakaoLogin(@Valid KakaoLoginRequest kakaoLoginRequest) {
        memberService.kakaoLogin(kakaoLoginRequest);
    }


    //회원 전체 조회
    @GetMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public List<MemberResponse> memberFindAll() {
        return memberService.memberFindAll();
    }

    //회원 수정
    @PutMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public void memberEdit(@PathVariable("memberId") Long memberId, @RequestBody @Valid MemberEditRequest memberEditRequest) {
        memberService.memberEdit(memberId, memberEditRequest);
    }

    //회원 삭제
    @DeleteMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void memberDelete(@PathVariable("memberId") Long memberId) {
        memberService.memberDelete(memberId);
    }

}
