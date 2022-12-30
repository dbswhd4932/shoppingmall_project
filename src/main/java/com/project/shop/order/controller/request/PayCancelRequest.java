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
public class PayCancelRequest {

    @NotNull(message = "주문번호를 입력하세요.")
    private String merchantId;

    @NotNull(message = "취소사유를 입력하세요.")
    private String cancelReason;

}
