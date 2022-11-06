package com.project.shop.member.domain.entity;

import com.project.shop.global.common.BaseEntityTime;
import com.project.shop.member.domain.request.CardCreateDto;
import com.project.shop.member.domain.request.MemberSignupDto;
import com.project.shop.member.domain.request.MemberUpdateDto;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends BaseEntityTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;            //회원번호(PK)

    @Column(nullable = false, length = 20, unique = true)
    private String loginId;     //회원ID

    @Column(nullable = false, length = 50)
    private String password;    //비밀번호

    @OneToMany(mappedBy = "member") // 양방향 todo 권한 확인
    private List<Role> roles = new ArrayList<>();

    @Column(nullable = false, length = 20)
    private String name;        //이름

    @Embedded
    private Address address; // 주소

    @Column(nullable = false, length = 50, unique = true)
    private String email;       //이메일

    @Column(nullable = false, length = 20, unique = true)
    private String phone;       //핸드폰번호
    private LocalDateTime deletedAt; //회원탈퇴시간

    @OneToMany(mappedBy = "member")
    private List<Card> cards = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;          //장바구니(일대일)


    @Builder
    public Member(Long id, String loginId, String password, List<Role> roles, String name, Address address, String email, String phone, LocalDateTime deletedAt, List<Card> cards, Cart cart) {
        this.id = id;
        this.loginId = loginId;
        this.password = password;
        this.roles = roles;
        this.name = name;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.deletedAt = deletedAt;
        this.cards = cards;
        this.cart = cart;
    }

    public Member(MemberSignupDto memberSignupDto) {
        this.loginId = memberSignupDto.getLoginId();
        this.password = memberSignupDto.getPassword();
        this.name = memberSignupDto.getName();
        this.address = memberSignupDto.getAddress();
        this.email = memberSignupDto.getEmail();
        this.phone = memberSignupDto.getPhone();
    }

    public void update(MemberUpdateDto memberUpdateDto) {
        this.password = memberUpdateDto.getPassword();
        this.address = memberUpdateDto.getAddress();
        this.email = memberUpdateDto.getEmail();
        this.phone = memberUpdateDto.getPhone();
    }

    public void cardMapping(CardCreateDto cardCreateDto) {
        this.cards = cardCreateDto.getMember().getCards();
    }


}
