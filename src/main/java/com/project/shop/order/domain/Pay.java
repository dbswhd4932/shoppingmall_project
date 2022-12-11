package com.project.shop.order.domain;

import com.project.shop.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pay")
@Entity
public class Pay extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pay_id")
    private Long id;                //결제번호(PK)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;            //주문(일대일)

    @Column(nullable = false)
    private String cardCompany;     // 카드사

    @Column(nullable = false)
    private String cardNumber;      // 카드일련번호

    @Column(nullable = false)
    private int payPrice;          //결제가격

    @Builder
    public Pay(String cardCompany, String cardNumber, Order order, int payPrice) {
        this.cardCompany = cardCompany;
        this.cardNumber = cardNumber;
        this.order = order;
        this.payPrice = payPrice;
    }
}
