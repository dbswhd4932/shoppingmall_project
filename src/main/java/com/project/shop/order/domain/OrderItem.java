package com.project.shop.order.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.domain.Goods;
import com.project.shop.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.util.function.Supplier;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_item")
@Entity
public class OrderItem extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;            //주문상품번호(PK)

    private Long memberId;      // 회원 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;          //상품(다대일)

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    private Order order;          //주문(다대일)

    @Column(nullable = false)
    private int amount;           //주문수량개수

    @Column(nullable = false)
    private int orderPrice;      //주문수량가격

    @Builder
    public OrderItem(Long memberId, Goods goods, Order order, int amount, int orderPrice) {
        this.memberId = memberId;
        this.goods = goods;
        this.order = order;
        this.amount = amount;
        this.orderPrice = orderPrice;
    }

    // 주문_상품 생성
    public static OrderItem createOrderItem(Member member, Goods goods, int orderPrice , int amount, Order order) {
        return OrderItem.builder()
                .memberId(member.getId())
                .order(order)
                .goods(goods)
                .amount(amount)
                .orderPrice(orderPrice)
                .build();
    }
}
