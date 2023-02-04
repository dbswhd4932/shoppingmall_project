package com.project.shop.goods.controller.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsSearchCondition {

    private String categoryName;
    private Integer priceMin;
    private Integer priceMax;

    public GoodsSearchCondition(String categoryName, Integer priceMin, Integer priceMax) {
        this.categoryName = categoryName;
        this.priceMin = priceMin;
        this.priceMax = priceMax;
    }
}
