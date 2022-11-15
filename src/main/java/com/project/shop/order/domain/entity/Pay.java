package com.project.shop.order.domain.entity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.entity.Card;
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

    private Long cardId;            // 카드 ID

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;            //주문(일대일)

    private int price;              //결제가격

    @Builder
    public Pay(Long cardId, Order order, int price) {
        this.cardId = cardId;
        this.order = order;
        this.price = price;
    }
}
