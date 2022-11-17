package com.project.shop.goods.controller.request;

import com.project.shop.goods.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsCreateRequest {

    private String goodsName;
    private Long memberId;
    private Category category;
    private int price;
    private String description;

}
