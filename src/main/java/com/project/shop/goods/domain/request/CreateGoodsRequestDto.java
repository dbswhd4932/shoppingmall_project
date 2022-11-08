package com.project.shop.goods.domain.request;

import com.project.shop.goods.domain.enetity.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGoodsRequestDto {

    private String name;
    private Category category;
    private int price;
    private String description;
}
