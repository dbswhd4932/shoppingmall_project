package com.project.shop.goods.controller.request;

import com.project.shop.goods.domain.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoodsSearchCondition {

    private Integer priceMin;
    private Integer priceMax;
    private String category;

    public GoodsSearchCondition(Integer priceMin, Integer priceMax, String category) {
        this.priceMin = priceMin;
        this.priceMax = priceMax;
        this.category = category;
    }
}
