package com.project.shop.goods.service;

import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    // 리뷰생성
    void reviewCreate(ReviewCreateRequest reviewCreateRequest);

    // 리뷰 전체조회
    List<ReviewResponse> reviewFindAll(Pageable pageable);


    // 리뷰 수정
    void reviewEdit(Long reviewId, Long memberId, ReviewEditRequest reviewEditRequest);

    // 리뷰 삭제
    void reviewDelete(Long reviewId, Long memberId);
}
