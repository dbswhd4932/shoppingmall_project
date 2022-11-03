package com.project.shop.member.domain.entity;

import com.project.shop.global.common.BaseEntityTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "card")
@Entity
public class Card extends BaseEntityTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "card_id")
    private Long id;        //카드번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;      //회원번호 다대일

    private String cardCompany; //카드회사

    @Column(unique = true)
    private String cardNumber;  //카드일련번호
    private String cardExpire;  //카드만료일


}
