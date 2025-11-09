package com.project.shop.member.controller.response;

import com.project.shop.goods.domain.Goods;
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

    private Long cartId;        // 장바구니 ID
    private Long goodsId;       // 상품 ID
    private String goodsName;   // 상품명
    private String imageUrl;    // 상품 대표 이미지
    private int price;          // 상품 개당 가격
    private int totalAmount;    // 수량
    private int totalPrice;     // 총 가격 (price * totalAmount)
    private Long optionNumber;  // 옵션 번호

    public static CartResponse of(Cart cart, Goods goods) {
        String imageUrl = null;
        if (goods.getImages() != null && !goods.getImages().isEmpty()) {
            imageUrl = goods.getImages().get(0).getFileUrl();
        }

        return CartResponse.builder()
                .cartId(cart.getId())
                .goodsId(goods.getId())
                .goodsName(goods.getGoodsName())
                .imageUrl(imageUrl)
                .price(goods.getPrice())
                .totalAmount(cart.getTotalAmount())
                .totalPrice(cart.getTotalPrice())
                .optionNumber(cart.getOptionNumber())
                .build();
    }

}
