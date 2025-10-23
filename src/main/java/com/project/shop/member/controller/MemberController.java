package com.project.shop.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.shop.member.controller.request.LoginIdCheckRequest;
import com.project.shop.member.controller.request.LoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.global.config.security.JwtTokenDto;
import com.project.shop.member.service.MemberService;
import io.swagger.annotations.ApiOperation;
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
    @ApiOperation(value = "회원 가입")
    public void memberSignup(@RequestBody @Valid MemberSignupRequest request) {
        memberService.memberSignup(request);
    }

    //로그인 아이디 중복 체크
    @PostMapping("/members/exist")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "로그인아이디 중복 체크")
    public void memberLoginIdDuplicateCheck(@RequestBody @Valid LoginIdCheckRequest request) {
        memberService.loginIdDuplicateCheck(request.getLoginId());
    }

    //일반 로그인
    @PostMapping("/members/login")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "일반 로그인")
    public JwtTokenDto login(@RequestBody LoginRequest loginRequest) throws JsonProcessingException {
        return memberService.login(loginRequest);
    }

    // 내 정보 가져오기
    @GetMapping("/members/me")
    @PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "내 정보 조회")
    public MemberResponse findByDetailMyInfo() {
        return memberService.findByDetailMyInfo();
    }

    //회원 수정
    @PutMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
    @ApiOperation(value = "회원 수정")
    public void memberEdit(@RequestBody @Valid MemberEditRequest memberEditRequest) {
        memberService.memberEdit(memberEditRequest);
    }

    //회원 탈퇴
    @DeleteMapping("/members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER','SELLER','ADMIN')")
    @ApiOperation(value = "회원 탈퇴")
    public void memberDelete() {
        memberService.memberDelete();
    }
}
