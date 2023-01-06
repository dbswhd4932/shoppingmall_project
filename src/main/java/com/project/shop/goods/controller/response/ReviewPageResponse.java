package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewPageResponse {

    private Long memberId;
    private Long goodsId;
    private String comment;
    private int totalPage;
    private int totalCount;
    private int pageNumber;
    private int currentPageSize;

    public static ReviewPageResponse toResponse(Review review, Page page) {
        return ReviewPageResponse.builder()
                .memberId(review.getMemberId())
                .goodsId(review.getGoods().getId())
                .comment(review.getComment())
                .totalPage(page.getTotalPages())
                .totalCount((int) page.getTotalElements())
                .pageNumber(page.getNumber())
                .currentPageSize(page.getSize())
                .build();
    }

}
