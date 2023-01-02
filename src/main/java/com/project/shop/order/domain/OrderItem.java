package com.project.shop.order.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.member.domain.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;               //주문상품번호(PK)

    @Column(nullable = false)
    private Long memberId;        // 회원 ID

    @Column(nullable = false)
    private Long goodsId;         //상품(다대일)

    @Column(nullable = false)
    private String goodsName;     // 상품 이름

    @Column(nullable = false)
    private int price;            // 상품 가격

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    private Order order;          //주문(다대일)

    @Column(nullable = false)
    private int amount;           //주문수량개수

    @Column(nullable = false)
    private int orderPrice;      //주문수량가격

    @Builder
    public OrderItem(Long memberId, Long goodsId, String goodsName, int price, Order order, int amount, int orderPrice) {
        this.memberId = memberId;
        this.goodsId = goodsId;
        this.goodsName = goodsName;
        this.price = price;
        this.order = order;
        this.amount = amount;
        this.orderPrice = orderPrice;
    }

    // 주문_상품 생성
    public static OrderItem createOrderItem(Member member, Long goodsId, int orderPrice , int amount, Order order, String goodsName, int price) {
        return OrderItem.builder()
                .memberId(member.getId())
                .order(order)
                .goodsId(goodsId)
                .amount(amount)
                .orderPrice(orderPrice)
                .goodsName(goodsName)
                .price(price)
                .build();
    }
}
