package com.project.shop.member.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.controller.request.KakaoLoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;                 //회원번호(PK)

    @Column(nullable = false, length = 20, unique = true)
    private String loginId;         //회원ID

    @Column(nullable = false)
    private String password;        //비밀번호

    @Column(nullable = false, length = 20)
    private String name;            //이름

    @Column(nullable = false)
    private String zipcode;         //우편번호

    @Column(nullable = false)
    private String detailAddress;   //상세주소

    @Column(nullable = false, length = 50)
    @Email
    private String email;           //이메일

    @Column(nullable = false, length = 20)
    private String phone;           //핸드폰번호

    @Column
    private LocalDateTime deletedAt;//회원탈퇴시간

    @Enumerated(EnumType.STRING)
    private LoginType loginType;    //로그인타입 ( NO_SOCIAL , KAKAO )

    @OneToMany(mappedBy = "member")
    private List<Role> roles = new ArrayList<>();

    @Builder
    public Member(String loginId, String password, String name, String zipcode, String detailAddress, String email, String phone, LoginType loginType) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.zipcode = zipcode;
        this.detailAddress = detailAddress;
        this.email = email;
        this.phone = phone;
        this.loginType = loginType;
    }

    // 회원 생성
    public static Member create(MemberSignupRequest memberSignupRequest, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .loginId(memberSignupRequest.getLoginId())
                .password(passwordEncoder.encode(memberSignupRequest.getPassword()))
                .name(memberSignupRequest.getName())
                .zipcode(memberSignupRequest.getZipcode())
                .detailAddress(memberSignupRequest.getDetailAddress())
                .email(memberSignupRequest.getEmail())
                .phone(memberSignupRequest.getPhone())
                .loginType(LoginType.NO_SOCIAL)
                .build();
    }

    // 카카오 로그인 -> 회원 생성
    public static Member kakaoCreate(KakaoLoginRequest kakaoLoginRequest, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .loginId(kakaoLoginRequest.getLoginId())
                .password(passwordEncoder.encode(kakaoLoginRequest.getLoginId()))
                .name("")
                .zipcode("")
                .detailAddress("")
                .email(kakaoLoginRequest.getEmail())
                .phone("")
                .loginType(LoginType.KAKAO)
                .build();
    }

    // 회원 수정
    public Member edit(MemberEditRequest memberEditRequest, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(memberEditRequest.getPassword());
        this.zipcode = memberEditRequest.getZipcode();
        this.detailAddress = memberEditRequest.getDetailAddress();
        this.email = memberEditRequest.getEmail();
        this.phone = memberEditRequest.getPhone();
        return this;
    }

    // 회원 탈퇴 시 deletedAt 초기화
    public void setDeletedAt() {
        this.deletedAt = LocalDateTime.now();
    }

}
