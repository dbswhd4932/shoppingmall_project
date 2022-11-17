package com.project.shop.goods.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequest {

    private Long orderItemId;
    private Long memberId;
    private String comment;
}
