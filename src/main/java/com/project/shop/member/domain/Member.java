package com.project.shop.member.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.controller.request.LoginRequest;
import com.project.shop.member.controller.request.MemberEditRequest;
import com.project.shop.member.controller.request.MemberSignupRequest;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Builder.Default // 빌더로 인스턴스 생성 시 초기화할 값을 정할 수 있다.
    private List<Role> roles = new ArrayList<>();

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
    public static Member kakaoCreate(LoginRequest loginRequest, PasswordEncoder passwordEncoder) {
        return Member.builder()
                .loginId(loginRequest.getLoginId())
                .password(passwordEncoder.encode(loginRequest.getPassword()))
                .name("")
                .zipcode("")
                .detailAddress("")
                .email(loginRequest.getEmail())
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

    // 회원 권한 적용
    public void setRoles(Role role) {
        this.getRoles().add(role);
    }

}
