package com.project.shop.member.controller;

import com.project.shop.member.domain.request.MemberSignupDto;
import com.project.shop.member.domain.request.MemberUpdateDto;
import com.project.shop.member.domain.response.MemberResponseDto;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public void signup(@Valid @RequestBody MemberSignupDto memberSignupDto){
        memberService.signup(memberSignupDto);
    }

    /**
     *  회원 전체 조회
     */
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponseDto>> findAll() {
        return ResponseEntity.ok(memberService.findAll());
    }

    /**
     *  회원 단건 조회
     */
    @GetMapping("/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponseDto findOne(@PathVariable("id") Long id) {
        return memberService.findOne(id);
    }

    /**
     *  회원 수정
     */
    @PutMapping("/members/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MemberResponseDto update(@PathVariable("id") Long id, @RequestBody MemberUpdateDto memberUpdateDto) {
        return memberService.update(id,memberUpdateDto);
    }

    /**
     *  회원 삭제
     */
    @DeleteMapping("/members/{id}")
    public void delete(@PathVariable("id") Long id) {
        memberService.delete(id);
    }


}
