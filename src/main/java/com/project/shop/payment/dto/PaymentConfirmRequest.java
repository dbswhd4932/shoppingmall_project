package com.project.shop.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmRequest {

    @NotNull(message = "결제 키는 필수입니다.")
    private String paymentKey;

    @NotNull(message = "주문 ID는 필수입니다.")
    private String orderId;

    @NotNull(message = "결제 금액은 필수입니다.")
    private Integer amount;
}
