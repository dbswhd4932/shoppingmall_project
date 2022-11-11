package com.project.shop.member.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartCreateRequest {

    private Long goodsId; // 상품 번호

    private int amount; // 구매 수량


}
