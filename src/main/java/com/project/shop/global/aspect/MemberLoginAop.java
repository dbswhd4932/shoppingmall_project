package com.project.shop.global.aspect;

import com.project.shop.member.controller.request.LoginRequest;
import com.project.shop.member.domain.MemberLoginHistory;
import com.project.shop.member.repository.MemberLoginHistoryRepository;
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
public class MemberLoginAop {

    private MemberLoginHistoryRepository memberLoginHistoryRepository;

    @Pointcut("execution(* com.project.shop.member.service.MemberService.login(*)) && args(loginRequest)")
    public void loginMember(LoginRequest loginRequest){}

    @AfterReturning(value = "loginMember(loginRequest)", argNames = "loginRequest")
    public void saveMemberHistory(LoginRequest loginRequest) {
        MemberLoginHistory memberLoginHistory = MemberLoginHistory.builder().loginId(loginRequest.getLoginId()).time(LocalDateTime.now()).build();
        memberLoginHistoryRepository.save(memberLoginHistory);
    }

}
