package com.project.shop.order.domain.entity.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayCreateRequest {

    private Long cardId;
    private Long memberId;
    private Long orderId;

}
