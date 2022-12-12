package com.project.shop.member.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.controller.request.KakaoLoginRequest;
import com.project.shop.member.controller.request.NoSocialLoginRequest;
import com.project.shop.member.domain.LoginType;
import com.project.shop.member.domain.Member;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import com.project.shop.member.controller.response.MemberResponse;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    // 회원생성
    @Override
    public void memberSignup(MemberSignupRequest memberSignupRequest) {
        if(memberRepository.findByLoginId(memberSignupRequest.getLoginId()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
        }

        Member member = Member.create(memberSignupRequest);
        memberRepository.save(member);
    }

    // 회원가입 중복체크
    @Override
    public void loginIdDuplicateCheck(String loginId) {
        if(memberRepository.findByLoginId(loginId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);
        }
    }

    // todo 일반 로그인
    @Override
    public void noSocialLogin(NoSocialLoginRequest noSocialLoginRequest) {
        memberRepository.findByLoginId(noSocialLoginRequest.getLoginId()).orElseThrow(
                ()-> new IllegalArgumentException("아이디를 확인해주세요.")
        );

        memberRepository.findByPassword(noSocialLoginRequest.getPassword()).orElseThrow(
                ()-> new IllegalArgumentException("비밀번호를 확인해주세요.")
        );
    }

    // todo 카카오 로그인
    @Override
    public void kakaoLogin(KakaoLoginRequest kakaoLoginRequest) {
        Member member = Member.builder()
                .loginId(kakaoLoginRequest.getKakaoLoginId())
                .email(kakaoLoginRequest.getKakaoEmail())
                .loginType(LoginType.KAKAO)
                .build();

        memberRepository.save(member);
    }

    // 회원 1명 조회
    @Transactional(readOnly = true)
    @Override
    public MemberResponse memberFindOne(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        return new MemberResponse().toResponse(member);
    }

    // 회원 전체 조회
    @Transactional(readOnly = true)
    @Override
    public List<MemberResponse> memberFindAll() {
        List<MemberResponse> responseList = memberRepository.findAll()
                .stream().map(member -> new MemberResponse().toResponse(member)).collect(Collectors.toList());
        return responseList;
    }

    // 회원 수정
    @Override
    public void memberEdit(Long memberId, MemberEditRequest memberEditRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        member.edit(memberEditRequest);
    }

    // 회원 탈퇴
    @Override
    public void memberDelete(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        member.setDeletedAt();
    }
}
