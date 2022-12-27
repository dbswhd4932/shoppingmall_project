package com.project.shop.goods.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCheckRequest {

    private Long goodsId;
    private int goodsPrice;
    private Long optionId;
    private int goodsTotalPrice;
}
