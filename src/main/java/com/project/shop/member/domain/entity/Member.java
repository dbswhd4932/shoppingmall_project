package com.project.shop.member.domain.entity;

import com.project.shop.global.common.BaseTimeEntity;
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

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

}
