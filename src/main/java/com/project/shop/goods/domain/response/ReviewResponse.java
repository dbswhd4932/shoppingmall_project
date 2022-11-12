package com.project.shop.goods.domain.response;

import com.project.shop.goods.domain.enetity.Review;
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

    public static ReviewResponse toReviewResponse(Review review) {
        return ReviewResponse.builder()
                .memberId(review.getMemberId())
                .goodsId(review.getGoods().getId())
                .comment(review.getComment())
                .build();
    }

}
