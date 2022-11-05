package com.project.shop.member.controller;

import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.MemberSignupDto;
import com.project.shop.member.domain.response.MemberResponse;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    /**
     *  회원가입
     */
    @PostMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse signup(@Valid @RequestBody MemberSignupDto memberSignupDto){
        memberService.signup(memberSignupDto);
        return new MemberResponse(new Member(memberSignupDto));
    }

    /**
     *  회원 전체 조회
     */
    @GetMapping("/members")
    @ResponseStatus(HttpStatus.OK)
    public List<MemberResponse> findAll() {
        return memberService.findAll();
    }

    /**
     *  회원 단건 조회
     */
    @GetMapping("/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponse findOne(@PathVariable("id") Long id) {
        return memberService.findOne(id);
    }



}
