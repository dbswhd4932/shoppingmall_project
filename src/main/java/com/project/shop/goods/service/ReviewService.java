package com.project.shop.goods.service;

import com.project.shop.goods.domain.request.ReviewCreateRequest;
import com.project.shop.goods.domain.request.ReviewEditRequest;
import com.project.shop.goods.domain.response.ReviewResponse;
import com.project.shop.member.domain.entity.Member;

import java.util.List;

public interface ReviewService {

    // 리뷰생성
    void reviewCreate(ReviewCreateRequest reviewCreateRequest);

    // 리뷰 전체조회
    List<ReviewResponse> reviewFindAll();

    // 리뷰 회원 별 조회
    List<ReviewResponse> reviewFindMember(Long memberId);

    // 리뷰 수정
    void reviewEdit(Long reviewId, Long memberId, ReviewEditRequest reviewEditRequest);

    // 리뷰 삭제
    void reviewDelete(Long reviewId);
}
