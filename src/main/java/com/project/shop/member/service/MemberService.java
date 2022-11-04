package com.project.shop.member.service;

import com.project.shop.member.domain.request.MemberSignupDto;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.MemberUpdateDto;
import com.project.shop.member.domain.response.MemberResponse;
import com.project.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
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
    public void signup(@RequestBody @Valid MemberSignupDto memberSignupDto) {
        Member member = Member.builder()
                .loginId(memberSignupDto.getLoginId())
                .password(memberSignupDto.getPassword())
                .name(memberSignupDto.getName())
                .zipcode(memberSignupDto.getZipcode())
                .detailAddress(memberSignupDto.getDetailAddress())
                .email(memberSignupDto.getEmail())
                .phone(memberSignupDto.getPhone())
                .build();

        memberRepository.save(member);
    }

    /**
     * 회원 전체조회
     */
    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream()
                .map(member -> new MemberResponse(member))
                .collect(Collectors.toList());
    }

    /**
     * 회원 단건 조회
     */
    @Transactional(readOnly = true)
    public MemberResponse findOne(Long id) {
        Member findMember = memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + id));

        return new MemberResponse(findMember);
    }

    /**
     * 회원 수정
     * 비밀번호, 이메일, 핸드폰번호, 주소(우편번호, 상세주소)
     */
    public void update(Long id, @RequestBody MemberUpdateDto memberUpdateDto) {
        Member findMember = memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + id));

        findMember.update(memberUpdateDto);
    }

    /**
     *  회원 삭제 todo 1) 회원정보 삭제?  2) 중요정보만 삭제하고 데이터 보관?
     */
    public void delete(Long id) {
        Member findMember = memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다. id = " + id));
        memberRepository.delete(findMember);
    }
}
