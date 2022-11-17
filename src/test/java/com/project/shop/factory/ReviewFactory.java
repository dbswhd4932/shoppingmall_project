package com.project.shop.factory;

import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.member.domain.Member;

public class ReviewFactory {

    public static Review createReview(Member member, Goods goods) {
        return Review.builder()
                .memberId(member.getId())
                .goods(goods)
                .comment("테스트 리뷰")
                .build();
    }

    public static ReviewCreateRequest reviewCreateRequest(Member member, Goods goods) {
        return ReviewCreateRequest.builder()
                .memberId(member.getId())
                .goodsId(goods.getId())
                .comment("테스트 리뷰")
                .build();
    }

    public static ReviewEditRequest reviewEditRequest(String comment) {
        return ReviewEditRequest.builder()
                .comment(comment)
                .build();
    }
}
