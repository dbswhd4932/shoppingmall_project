package com.project.shop.goods.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCheckRequest {

    private Long goodsId;
    private int goodsPrice;
    private Long optionId;
    private int goodsTotalPrice;
}
