package com.project.shop.member.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Option;
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

    private Long optionNumber;   //옵션 번호

    @Builder
    public Cart(Long id, Member member, Long goodsId, int totalAmount, int totalPrice, Long optionNumber) {
        this.id = id;
        this.member = member;
        this.goodsId = goodsId;
        this.totalAmount = totalAmount;
        this.totalPrice = totalPrice;
        this.optionNumber = optionNumber;
    }

    // 장바구니 수량, 옵션 변경
    public void edit(Option option, CartEditRequest cartEditRequest) {
        int goodsTotalPrice = option.getTotalPrice();
        this.totalAmount = cartEditRequest.getAmount();
        this.optionNumber = cartEditRequest.getOptionNumber();
        this.totalPrice = goodsTotalPrice * (cartEditRequest.getAmount());
    }

    public void editNoOption(Goods goods , CartEditRequest cartEditRequest) {
        this.totalAmount = cartEditRequest.getAmount();
        this.optionNumber = cartEditRequest.getOptionNumber();
        this.totalPrice = goods.getPrice() * (cartEditRequest.getAmount());
    }

}
