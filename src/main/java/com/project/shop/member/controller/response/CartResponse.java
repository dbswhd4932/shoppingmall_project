package com.project.shop.member.controller.response;

import com.project.shop.member.domain.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long memberId;
    private Long goodsId;
    private int totalAmount;
    private int totalPrice;
    private Long optionNumber;

    public CartResponse(Cart cart) {
        this.memberId = cart.getId();
        this.goodsId = cart.getGoodsId();
        this.totalAmount = cart.getTotalAmount();
        this.totalPrice = cart.getTotalPrice();
        this.optionNumber = cart.getOptionNumber();

    }

}
