package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long memberId;
    private Long goodsId;
    private String comment;

    public ReviewResponse(Review review) {
        this.memberId = review.getMemberId();
        this.goodsId = review.getGoods().getId();
        this.comment = review.getComment();
    }
}
