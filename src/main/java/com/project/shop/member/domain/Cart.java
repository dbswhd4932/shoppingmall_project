package com.project.shop.member.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.domain.Goods;
import com.project.shop.member.controller.request.CartCreateRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cart")
@Entity
public class Cart extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;        //장바구니 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private Long goodsId;       // 상품 Id

    @Column(nullable = false)
    private int totalAmount;    //장바구니 총 수량

    @Column(nullable = false)
    private int totalPrice;     //장바구니 총 가격

    @Builder
    public Cart(Member member, Long goodsId, int totalAmount, int totalPrice) {
        this.member = member;
        this.goodsId = goodsId;
        this.totalAmount = totalAmount;
        this.totalPrice = totalPrice;
    }

    // 장바구니 생성
    public static Cart createCart(Member member, Goods goods, CartCreateRequest cartCreateRequest) {
        return Cart.builder()
                .member(member)
                .goodsId(cartCreateRequest.getGoodsId())
                .totalAmount(cartCreateRequest.getAmount())
                .totalPrice(goods.getPrice() * cartCreateRequest.getAmount())
                .build();
    }

}
