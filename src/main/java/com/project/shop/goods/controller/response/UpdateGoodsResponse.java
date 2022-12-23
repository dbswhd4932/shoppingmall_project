package com.project.shop.goods.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGoodsResponse {

    private Long goodsId;
    private int goodsPrice;

}
