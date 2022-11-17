package com.project.shop.order.domain;

import com.project.shop.goods.domain.Goods;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_item")
@Entity
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;            //주문상품번호(PK)

    private Long memberId;      // 회원 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;          //상품(다대일)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;          //주문(다대일)

    private int amount;           //주문수량개수

    private int orderPrice;      //주문수량가격

    @Builder
    public OrderItem(Long memberId, Goods goods, Order order, int amount, int orderPrice) {
        this.memberId = memberId;
        this.goods = goods;
        this.order = order;
        this.amount = amount;
        this.orderPrice = orderPrice;
    }

    public static OrderItem createOrderItem(Long memberId, Goods goods, int orderPrice , int amount, Order order) {
        return OrderItem.builder()
                .memberId(memberId)
                .order(order)
                .goods(goods)
                .amount(amount)
                .orderPrice(orderPrice)
                .build();
    }
}