package com.project.shop.member.domain.entity;

import com.project.shop.global.common.BaseEntityTime;
import com.project.shop.goods.domain.enetity.Goods;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart")
@Entity
public class Cart extends BaseEntityTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cart_id")
    private Long id;        //장바구니 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;        //상품번호(다대일)

    private int totalAmount;    //장바구니 총 수량
    private int totalPrice;     //장바구니 총 가격

}
