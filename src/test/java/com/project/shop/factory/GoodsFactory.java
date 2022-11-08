package com.project.shop.factory;

import com.project.shop.goods.domain.enetity.Category;
import com.project.shop.goods.domain.enetity.Goods;

public class GoodsFactory {

    public static Goods createGoods() {

        return Goods.builder()
                .name("테스트상품")
                .category(new Category("테스트카테고리"))
                .price(1000)
                .description("테스트 상품 설명")
                .build();
    }

}
