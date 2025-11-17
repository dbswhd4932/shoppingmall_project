package com.project.shop.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelRequest {

    @NotNull(message = "결제 키는 필수입니다.")
    private String paymentKey;

    @NotNull(message = "취소 사유는 필수입니다.")
    private String cancelReason;
}
