package com.project.shop.factory;

import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;

public class GoodsFactory {

    public static Goods createGoods() {

        return Goods.builder()
                .id(1L)
                .memberId(1L)
                .goodsName("테스트상품")
                .category(new Category("테스트카테고리"))
                .price(1000)
                .description("테스트 상품 설명")
                .build();
    }

    public static GoodsCreateRequest createRequest() {
        return GoodsCreateRequest.builder()
                .goodsName("테스트상품")
                .optionCreateRequest(null)
                .category(new Category("테스트카테고리"))
                .goodsDescription("테스트 상품 설명")
                .price(10000)
                .build();
    }

}
