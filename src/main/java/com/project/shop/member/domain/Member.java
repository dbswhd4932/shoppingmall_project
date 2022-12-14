package com.project.shop.member.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity{

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

    private LocalDateTime deletedAt;//회원탈퇴시간

    @Enumerated(EnumType.STRING)
    private LoginType loginType;    //로그인타입 ( NO_SOCIAL , KAKAO )

    @ElementCollection(fetch = FetchType.EAGER) // 2개이상의 ROLE 이 들어갈 수 있다. ex ) USER, SELLER
    private List<String> roles = new ArrayList<>();

    @Builder
    public Member(String loginId, String password, String name, String zipcode, String detailAddress, String email, String phone, LoginType loginType, List<String> roles) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.zipcode = zipcode;
        this.detailAddress = detailAddress;
        this.email = email;
        this.phone = phone;
        this.loginType = loginType;
        this.roles = roles;
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
                .roles(memberSignupRequest.getRoles())
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

    // 회원 탈퇴 시 deletedAt 초기화
    public void setDeletedAt() {
        this.deletedAt = LocalDateTime.now();
    }

}
