package com.project.shop.member.domain.request;

import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.member.domain.entity.Member;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CartCreateDto {

    private Member member; // 회원 ID
    private Goods goods; // 상품 ID
    private int quantity; // 장바구니에 담을 수량

    @Builder
    public CartCreateDto(Member member, Goods goods, int quantity) {
        this.member = member;
        this.goods = goods;
        this.quantity = quantity;
    }
}
