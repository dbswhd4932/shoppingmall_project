package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long memberId;
    private Long goodsId;
    private String comment;
    private List<ReplyResponse> replyResponse;

    public ReviewResponse(Review review) {
        this.memberId = review.getMemberId();
        this.goodsId = review.getGoods().getId();
        this.comment = review.getComment();
        this.replyResponse = review.getReplies()
                .stream()
                .map(ReplyResponse::new).collect(Collectors.toList());
    }
}
