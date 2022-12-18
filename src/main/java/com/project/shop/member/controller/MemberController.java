package com.project.shop.member.controller;

import com.project.shop.global.util.SecurityUtil;
import com.project.shop.member.controller.request.KakaoLoginRequest;
import com.project.shop.member.controller.request.NoSocialLoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.jwt.JwtTokenDto;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    // 회원 가입
    @PostMapping("/members/signup")
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

    //일반 로그인
    @PostMapping("/members/login")
    @ResponseStatus(HttpStatus.OK)
    public JwtTokenDto login(@RequestBody NoSocialLoginRequest noSocialLoginRequest) {
        return memberService.login(noSocialLoginRequest);
    }

    //카카오 로그인
    @PostMapping("/members/kakaoLogin")
    @ResponseStatus(HttpStatus.OK)
    public void kakaoLogin(@RequestBody @Valid KakaoLoginRequest kakaoLoginRequest) {
        memberService.kakaoLogin(kakaoLoginRequest);
    }

    //카카오 토큰 얻기
    @PostMapping("/members/kakaoGetToken")
    @ResponseStatus(HttpStatus.OK)
    public JwtTokenDto kakaoGetToken(@RequestBody @Valid KakaoLoginRequest kakaoLoginRequest) {
        return memberService.kakaoGetToken(kakaoLoginRequest);
    }

    // 내 정보 가져오기
    @GetMapping("/member/me")
    @PreAuthorize("hasAnyRole('USER')")
    public MemberResponse findMemberInfoById() {
        return memberService.findMemberInfoById(SecurityUtil.getCurrentMemberId());
    }

    // 회원 1명 조회
    @GetMapping("/member/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse memberFindByMemberId(@PathVariable("memberId") Long memberId) {
        return memberService.memberFindByMemberId(memberId);
    }

    //회원 수정
    @PutMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER')")
    public void memberEdit(@PathVariable("memberId") Long memberId, @RequestBody @Valid MemberEditRequest memberEditRequest) {
        memberService.memberEdit(memberId, memberEditRequest);
    }

    //회원 탈퇴
    @DeleteMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER')")
    public void memberDelete(@PathVariable("memberId") Long memberId) {
        memberService.memberDelete(memberId);
    }
}
