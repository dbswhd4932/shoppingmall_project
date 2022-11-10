package com.project.shop.order.domain.entity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.entity.Card;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_cancel_pay")
@Entity
public class CancelPay extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_cancel_pay_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_cancel_id")
    private Cancel cancel;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    private int price;
    private String cancelMethod;
}
