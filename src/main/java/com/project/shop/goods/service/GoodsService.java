package com.project.shop.goods.service;

import com.project.shop.goods.domain.request.GoodsCreateRequest;
import com.project.shop.goods.domain.request.GoodsEditRequest;
import com.project.shop.goods.domain.response.GoodsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface GoodsService {

    // 상품 생성
    void goodsCreate(GoodsCreateRequest goodsCreateRequest, List<MultipartFile> files) throws IOException;

    // 상품 전체 검색 - 페이징 기능
    List<GoodsResponse> goodsFindAll(Pageable pageable);

    // 상품 검색 기능
    List<GoodsResponse> goodsFindKeyword(String keyword);

    // 상품 수정
    void goodsEdit(Long goodsId , GoodsEditRequest goodsEditRequest);

    // 상품 삭제
    void goodsDelete(Long goodsId);

}