package com.project.shop.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "주문번호는 필수입니다.")
    private String merchantId;

    @NotNull(message = "결제 금액은 필수입니다.")
    @Positive(message = "결제 금액은 100원 이상이어야 합니다.")
    private Integer amount;

    @NotNull(message = "주문명은 필수입니다.")
    private String orderName;

    // 고객 정보
    private String customerEmail;
    private String customerName;

    // 결제 방법 (CARD, VIRTUAL_ACCOUNT, TRANSFER 등)
    private String paymentMethod;
}
