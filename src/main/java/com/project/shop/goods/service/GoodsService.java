package com.project.shop.goods.service;

import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GoodsService {

    // 상품 생성 , 이미지 X
    void goodsCreate(GoodsCreateRequest goodsCreateRequest);

    // 상품 생성 , 이미지 O
    void goodsAndImageCreate(GoodsCreateRequest goodsCreateRequest, List<String> imgPaths);

    // 상품 전체 검색 - 페이징 기능
    List<GoodsResponse> goodsFindAll(Pageable pageable);

    // 상품 검색 기능
    List<GoodsResponse> goodsFindKeyword(Pageable pageable, String keyword);

    // 상품 수정
    void goodsEdit(Long goodsId, Long memberId , GoodsEditRequest goodsEditRequest);

    // 상품 삭제
    void goodsDelete(Long goodsId, Long memberId);

}
