package com.project.shop.goods.controller.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyCreateRequest {

    @NotNull(message = "리뷰번호를 입력하세요.")
    private Long reviewId;

    @NotNull(message = "상품을 등록한 회원번호를 입력하세요.")
    private Long productMemberId;

    @NotBlank(message = "댓글을 입력하세요.")
    private String replyComment;

}
