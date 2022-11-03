package com.project.shop.member.domain.entity;

import com.project.shop.global.common.BaseEntityTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member extends BaseEntityTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;            //회원번호(PK)

    @Column(nullable = false, length = 20, unique = true)
    private String loginId;     //회원ID

    @Column(nullable = false, length = 50)
    private String password;    //비밀번호

    @Column(nullable = false, length = 20)
    private String name;        //이름

    private String zipcode;     //우편번호
    private String detailAddress; //상세주소

    @Column(nullable = false, length = 50, unique = true)
    private String email;       //이메일

    @Column(nullable = false, length = 20, unique = true)
    private String phone;       //핸드폰번호
    private LocalDateTime deletedAt; //회원탈퇴시간

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;          //장바구니(일대일)



}
