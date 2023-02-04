package com.project.shop.goods.repository;

import com.project.shop.goods.controller.request.GoodsSearchCondition;
import com.project.shop.goods.controller.response.GoodsResponse;

import java.util.List;

public interface GoodsRepositoryCustom {

    List<GoodsResponse> searchCategoryAndBetweenPrice(GoodsSearchCondition condition);
}
