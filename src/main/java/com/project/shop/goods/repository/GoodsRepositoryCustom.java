package com.project.shop.goods.repository;

import com.project.shop.goods.controller.request.GoodsSearchCondition;
import com.project.shop.goods.controller.response.GoodsResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GoodsRepositoryCustom {

    Page<GoodsResponse> searchBetweenPrice(GoodsSearchCondition condition, Pageable pageable);
}
