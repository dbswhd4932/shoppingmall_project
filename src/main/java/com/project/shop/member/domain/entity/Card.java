package com.project.shop.member.domain.entity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.request.CardCreateRequest;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "card")
@Entity
public class Card extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;        //카드번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;      //회원(다대일)

    @Column(nullable = false)
    private String cardCompany; //카드회사

    @Column(nullable = false, unique = true)
    private String cardNumber;  //카드일련번호

    @Column(nullable = false)
    private String cardExpire;  //카드만료일

    @Builder
    public Card(Member member, String cardCompany, String cardNumber, String cardExpire) {
        this.member = member;
        this.cardCompany = cardCompany;
        this.cardNumber = cardNumber;
        this.cardExpire = cardExpire;
    }

}
