package com.project.shop.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartCreateRequest {

    @NotNull(message = "상품번호를 입력하세요.")
    private Long goodsId; // 상품 번호

    @NotNull(message = "구매 수량을 입력하세요.")
    private int amount; // 구매 수량


}
