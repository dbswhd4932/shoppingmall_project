package com.project.shop.member.service;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.domain.request.MemberSignupDto;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.MemberUpdateDto;
import com.project.shop.member.domain.response.MemberResponseDto;
import com.project.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
     *  todo 패스워드 암호화
     */
    public MemberResponseDto signup(@RequestBody MemberSignupDto memberSignupDto) {
        Member member = new Member(memberSignupDto);
        validateDuplicatedMember(member);
        memberRepository.save(member);
        return new MemberResponseDto(member);
    }

    private void validateDuplicatedMember(Member member) {
        memberRepository.findByLoginId(member.getLoginId())
                .ifPresent(m -> { // 같은 LoginId 가 존재하면 예외발생
                    throw new BusinessException(ErrorCode.DUPLICATED_LOGIN_ID);});
        memberRepository.findByEmail(member.getEmail())
                .ifPresent(m -> { // 같은 Email 이 존재하면 예외발생
                    throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);});
    }

    /**
     * 회원 전체조회
     */
    @Transactional(readOnly = true)
    public List<MemberResponseDto> findAll() {
        return memberRepository.findAll().stream()
                .map(member -> new MemberResponseDto(member))
                .collect(Collectors.toList());
    }

    /**
     * 회원 단건 조회
     */
    @Transactional(readOnly = true)
    public MemberResponseDto findOne(Long id) {
        Member findMember = memberRepository.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        return new MemberResponseDto(findMember);
    }

    /**
     * 회원 수정
     * 비밀번호, 이메일, 핸드폰번호, 주소(우편번호, 상세주소)
     */
    public MemberResponseDto update(Long id, @RequestBody MemberUpdateDto memberUpdateDto) {
        Member member = memberRepository.findById(id).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        member.update(memberUpdateDto);
        return new MemberResponseDto(member);
    }

    /**
     * 회원 삭제 todo 1) 회원정보 삭제?  2) 중요정보만 삭제하고 데이터 보관?
     */
    public void delete(Long id) {
        Member findMember = memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + id));
        memberRepository.delete(findMember);
    }

}
