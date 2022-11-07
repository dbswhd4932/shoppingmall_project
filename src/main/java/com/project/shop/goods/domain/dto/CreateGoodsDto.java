package com.project.shop.goods.domain.dto;

import com.project.shop.goods.domain.enetity.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateGoodsDto {

    private ItemCategory itemCategory;
    private String name;
    private int price;
    private String description;
    private String img1;
    private String img2;
    private String img3;

}
