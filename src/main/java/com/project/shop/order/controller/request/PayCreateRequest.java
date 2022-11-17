package com.project.shop.order.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayCreateRequest {

    @NotNull(message = "카드번호를 입력하세요.")
    private Long cardId;

    @NotNull(message = "회원번호를 입력하세요.")
    private Long memberId;

    @NotNull(message = "주문번호를 입력하세요.")
    private Long orderId;

}
