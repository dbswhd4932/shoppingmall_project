package com.project.shop.goods.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsSearchCondition {

    private Integer priceMin;
    private Integer priceMax;

    public GoodsSearchCondition(Integer priceMin, Integer priceMax) {
        this.priceMin = priceMin;
        this.priceMax = priceMax;
    }
}
