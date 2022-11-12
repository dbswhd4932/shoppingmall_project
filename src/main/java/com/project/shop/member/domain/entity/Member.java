package com.project.shop.member.domain.entity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.request.MemberEditRequest;
import com.project.shop.member.domain.request.MemberSignupRequest;
import com.project.shop.member.domain.response.MemberResponse;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;                 //회원번호(PK)

    @Column(nullable = false, length = 20, unique = true)
    private String loginId;         //회원ID

    @Column(nullable = false, length = 50)
    private String password;        //비밀번호

    @Column(nullable = false, length = 20)
    private String name;            //이름

    private String zipcode;         //우편번호

    private String detailAddress;   //상세주소

    @Column(nullable = false, length = 50, unique = true)
    private String email;           //이메일

    @Column(nullable = false, length = 20, unique = true)
    private String phone;           //핸드폰번호

    private LocalDateTime deletedAt;//회원탈퇴시간

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Builder
    public Member(String loginId, String password, String name, String zipcode, String detailAddress, String email, String phone, Role role) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.zipcode = zipcode;
        this.detailAddress = detailAddress;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    // 회원 생성
    public static Member create(MemberSignupRequest memberSignupRequest) {
        return Member.builder()
                .loginId(memberSignupRequest.getLoginId())
                .password(memberSignupRequest.getPassword())
                .name(memberSignupRequest.getName())
                .zipcode(memberSignupRequest.getZipcode())
                .detailAddress(memberSignupRequest.getDetailAddress())
                .email(memberSignupRequest.getEmail())
                .phone(memberSignupRequest.getPhone())
                .role(memberSignupRequest.getRole())
                .build();
    }

    // 회원 수정
    public Member edit(MemberEditRequest memberEditRequest) {
        this.password = memberEditRequest.getPassword();
        this.zipcode = memberEditRequest.getZipcode();
        this.detailAddress = memberEditRequest.getDetailAddress();
        this.email = memberEditRequest.getEmail();
        this.phone = memberEditRequest.getPhone();
        return this;
    }

}
