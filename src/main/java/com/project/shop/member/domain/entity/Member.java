package com.project.shop.member.domain.entity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.request.MemberSignupDto;
import com.project.shop.member.domain.request.MemberUpdateDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;                 //회원번호(PK)

    @Column(nullable = false, length = 20, unique = true)
    private String loginId;         //회원ID

    @Column(nullable = false, length = 50)
    private String password;        //비밀번호

    @Column(nullable = false, length = 20)
    private String name;            //이름

    @Embedded
    private Address address;        // 주소

    @Column(nullable = false, length = 50, unique = true)
    private String email;           //이메일

    @Column(nullable = false, length = 20, unique = true)
    private String phone;           //핸드폰번호
    private LocalDateTime deletedAt;//회원탈퇴시간

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;              //장바구니(일대일)

    @OneToMany(mappedBy = "member")
    private List<MemberRole> memberRoles = new ArrayList<>();

    @Builder
    public Member(String loginId, String password, String name, Address address, String email, String phone, LocalDateTime deletedAt, Cart cart, List<MemberRole> memberRoles) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.deletedAt = deletedAt;
        this.cart = cart;
        this.memberRoles = memberRoles;
    }

    public Member(MemberSignupDto memberSignupDto) {
        this.loginId = memberSignupDto.getLoginId();
        this.password = memberSignupDto.getPassword();
        this.name = memberSignupDto.getName();
        this.address = memberSignupDto.getAddress();
        this.email = memberSignupDto.getEmail();
        this.phone = memberSignupDto.getPhone();
//        this.memberRoles = memberSignupDto.getMemberRoles();
    }

    public void update(MemberUpdateDto memberUpdateDto) {
        this.password = memberUpdateDto.getPassword();
        this.address = memberUpdateDto.getAddress();
        this.email = memberUpdateDto.getEmail();
        this.phone = memberUpdateDto.getPhone();
    }

}
