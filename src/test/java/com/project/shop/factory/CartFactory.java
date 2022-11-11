package com.project.shop.factory;

import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.member.domain.entity.Cart;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CartCreateRequest;

public class CartFactory {

    public static CartCreateRequest cartCreateRequest(Goods goods, int amount) {
        return CartCreateRequest.builder()
                .goodsId(goods.getId())
                .amount(amount)
                .build();
    }

    public static Cart cartCreate(Member member, Goods goods) {
        return Cart.builder()
                .member(member)
                .goodsId(goods.getId())
                .totalAmount(2)
                .totalPrice(goods.getPrice() * 2)
                .build();
    }
}
