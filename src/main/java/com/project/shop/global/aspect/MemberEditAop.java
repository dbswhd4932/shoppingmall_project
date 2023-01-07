package com.project.shop.global.aspect;

import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.domain.Member;
import com.project.shop.member.domain.MemberEditHistory;
import com.project.shop.member.repository.MemberEditHistoryRepository;
import com.project.shop.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component // Bean 으로 등록
@AllArgsConstructor
@Slf4j
public class MemberEditAop {

    private MemberEditHistoryRepository memberEditHistoryRepository;
    private MemberRepository memberRepository;

    @Pointcut("execution(* com.project.shop.member.service.MemberService.memberEdit(*)) && args(memberEditRequest)")
    public void updateMember(MemberEditRequest memberEditRequest){}

    @AfterReturning(value = "updateMember(memberEditRequest)", argNames = "memberEditRequest")
    public void saveMemberHistory(MemberEditRequest memberEditRequest) {
        Member member = memberRepository.findByEmail(memberEditRequest.getEmail()).get();
        MemberEditHistory memberEditHistory = MemberEditHistory.builder().memberId(member.getId()).time(LocalDateTime.now()).build();
        memberEditHistoryRepository.save(memberEditHistory);
    }

}
