package com.project.shop.order.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.domain.Goods;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_cancel")
@Entity
public class Cancel extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_cancel_id")
    private Long id;            //주문취소번호(PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;        //주문(다대일)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;        //상품(다대일)

    private int totalAmount;    //주문수량
    private int totalPrice;     //주문가격

}
