package com.project.shop.order.domain.entity;

import com.project.shop.goods.domain.enetity.Goods;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_item_id")
@Entity
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_item_id")
    private Long id;            //주문상품번호(PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;        //주문(다대일)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;        //상품(다대일)

    private int totalAmount;    //주문수량개수

    private int totalPrice;     //주문수량가격




}
