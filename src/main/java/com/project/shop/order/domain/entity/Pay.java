package com.project.shop.order.domain.entity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.entity.Card;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_pay")
@Entity
public class Pay extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_pay_id")
    private Long id;        //결제번호(PK)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;            //주문(일대일)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;              //카드(일대일)

    private int price;              //결제가격

}
