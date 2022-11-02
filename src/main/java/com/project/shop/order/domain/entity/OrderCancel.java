package com.project.shop.order.domain.entity;

import com.project.shop.global.common.BaseEntityTime;
import com.project.shop.goods.domain.enetity.Goods;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_cancel")
@Entity
public class OrderCancel extends BaseEntityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_cancel_id")
    private Long id;            //주문취소번호(PK)

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;        //주문(다대일)

    @ManyToOne
    @JoinColumn(name = "goods_id")
    private Goods goods;        //상품(다대일)

    private int totalAmount;    //주문수량
    private int totalPrice;     //주문가격

}
