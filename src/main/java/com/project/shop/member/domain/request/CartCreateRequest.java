package com.project.shop.member.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CartCreateRequest {

    private Long goodsId; // 상품 번호

    private int totalAmount; // 구매 수량


}
