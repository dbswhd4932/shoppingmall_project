package com.project.shop.member.controller;

import com.project.shop.member.domain.request.MemberEditRequest;
import com.project.shop.member.domain.request.MemberSignupRequest;
import com.project.shop.member.domain.response.MemberResponse;
import com.project.shop.member.service.Impl.MemberServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberServiceImpl memberService;

    //회원 생성
    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public void memberSignup(@RequestBody MemberSignupRequest request) {
        memberService.memberSignup(request);
    }

    //회원 1명 조회
    @GetMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse memberFindOne(@PathVariable("memberId") Long memberId) {
        return memberService.memberFindOne(memberId);
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
    public void memberEdit(@PathVariable("memberId") Long memberId, @RequestBody MemberEditRequest memberEditRequest) {
        memberService.memberEdit(memberId, memberEditRequest);
    }

    //회원 삭제
    @DeleteMapping("/members/{memberId}")
    @ResponseStatus(HttpStatus.OK)
    public void memberDelete(@PathVariable("memberId") Long memberId) {
        memberService.memberDelete(memberId);
    }



}
