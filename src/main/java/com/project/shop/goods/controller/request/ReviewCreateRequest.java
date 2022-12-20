package com.project.shop.goods.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequest {

    @NotNull(message = "주문상품번호를 입력하세요.")
    private Long orderItemId;

//    @NotNull(message = "회원번호를 입력하세요.")
//    private Long memberId;

    @NotBlank(message = "댓글을 입력하세요.")
    private String comment;
}
