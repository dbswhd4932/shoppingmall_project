package com.project.shop.member.controller.response;

import com.project.shop.member.domain.Cart;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartPageResponse {

    private Long memberId;
    private Long goodsId;
    private int totalAmount;
    private int totalPrice;
    private int totalPage;
    private int totalCount;
    private int pageNumber;
    private int currentPageSize;

    public static CartPageResponse toResponse(Cart cart, Page page) {
        return CartPageResponse.builder()
                .memberId(cart.getMember().getId())
                .goodsId(cart.getGoodsId())
                .totalAmount(cart.getTotalAmount())
                .totalPrice(cart.getTotalPrice())
                .totalPage(page.getTotalPages())
                .totalCount((int) page.getTotalElements())
                .pageNumber(page.getNumber())
                .currentPageSize(page.getSize())
                .build();
    }
}
