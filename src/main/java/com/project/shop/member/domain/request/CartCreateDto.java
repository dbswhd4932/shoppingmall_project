package com.project.shop.member.domain.request;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class CartCreateDto {

    private Long memberId; // 회원 ID
    private Long goodsId; // 상품 ID
    private int quantity; // 장바구니에 담을 수량

    @Builder
    public CartCreateDto(Long memberId, Long goodsId, int quantity) {
        this.memberId = memberId;
        this.goodsId = goodsId;
        this.quantity = quantity;
    }
}
