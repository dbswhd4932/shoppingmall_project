package com.project.shop.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private String merchantId;        // 주문번호
    private String paymentKey;      // PG사 결제 키
    private String orderId;         // PG사 주문 ID
    private Integer amount;         // 결제 금액
    private String status;          // 결제 상태
    private String method;          // 결제 수단
    private LocalDateTime approvedAt; // 승인 시간
    private String receiptUrl;      // 영수증 URL
}
