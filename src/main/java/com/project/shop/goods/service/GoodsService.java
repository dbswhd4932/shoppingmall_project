package com.project.shop.goods.service;

import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.goods.controller.request.UpdateCheckRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.controller.response.UpdateGoodsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface GoodsService {

    // 상품 생성 , 이미지 O
    void goodsCreate(GoodsCreateRequest goodsCreateRequest,  List<MultipartFile> imgPaths) throws IOException;

    // 상품 전체 검색 - 페이징 기능
    List<GoodsResponse> goodsFindAll(Pageable pageable);

    // 상품 가격 변경 확인
    List<UpdateGoodsResponse> checkGoodsUpdate(List<UpdateCheckRequest> updateCheckRequest);

    // 상품 상세(정보) 조회
    GoodsResponse goodsDetailFind(Long goodsId);

    // 상품 검색 기능
    List<GoodsResponse> goodsFindKeyword(String keyword, Pageable pageable);

    // 상품 수정
    void goodsEdit(Long goodsId, GoodsEditRequest goodsEditRequest, List<MultipartFile> imgPaths);

    // 상품 삭제
    void goodsDelete(Long goodsId);

}
