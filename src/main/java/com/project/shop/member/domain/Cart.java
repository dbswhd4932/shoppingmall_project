package com.project.shop.member.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.domain.Goods;
import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
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

    private int optionNumber;   //옵션 번호

    @Builder
    public Cart(Member member, Long goodsId, int totalAmount, int totalPrice, int optionNumber) {
        this.member = member;
        this.goodsId = goodsId;
        this.totalAmount = totalAmount;
        this.totalPrice = totalPrice;
        this.optionNumber = optionNumber;
    }

    // 장바구니 생성
    public static Cart createCart(Member member, Goods goods, CartCreateRequest cartCreateRequest) {
        int goodsTotalPrice = goods.getPrice();

        // 옵션이 있는 상품이면 상품 최종 가격변경(기본상품 + 옵션가격)
        if (!goods.getOptions().isEmpty()) {
            goodsTotalPrice = goods.getOptions().get(cartCreateRequest.getOptionNumber()-1).getTotalPrice();
        }

        return Cart.builder()
                .member(member)
                .goodsId(cartCreateRequest.getGoodsId())
                .totalAmount(cartCreateRequest.getAmount())
                .totalPrice(goodsTotalPrice * cartCreateRequest.getAmount())
                .optionNumber(cartCreateRequest.getOptionNumber())
                .build();
    }

    // 장바구니 수량, 옵션 변경
    public void edit(Goods goods, CartEditRequest cartEditRequest) {
        int goodsTotalPrice = goods.getOptions().get(cartEditRequest.getOptionNumber()-1).getTotalPrice();
        this.totalAmount = cartEditRequest.getAmount();
        this.optionNumber = cartEditRequest.getOptionNumber();
        this.totalPrice = goodsTotalPrice * (cartEditRequest.getAmount());
    }

}
