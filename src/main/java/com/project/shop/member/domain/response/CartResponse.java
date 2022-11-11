package com.project.shop.member.domain.response;

import com.project.shop.member.domain.entity.Cart;
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

    public static CartResponse toCartResponse(Cart cart) {
        return CartResponse.builder()
                .memberId(cart.getMember().getId())
                .goodsId(cart.getGoodsId())
                .totalAmount(cart.getTotalAmount())
                .totalPrice(cart.getTotalPrice())
                .build();
    }
}
