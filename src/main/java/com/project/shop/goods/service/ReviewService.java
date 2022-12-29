package com.project.shop.goods.service;

import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    // 리뷰생성
    void reviewCreate(Long orderItemId, ReviewCreateRequest reviewCreateRequest);

    // 리뷰 전체
    List<ReviewResponse> reviewFindAll(Long goodsId, Pageable pageable);

    // 리뷰 수정
    void reviewEdit(Long reviewId, ReviewEditRequest reviewEditRequest);

    // 리뷰 삭제
    void reviewDelete(Long reviewId);
}
