package com.project.shop.payment.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.order.domain.Order;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String merchantId;        // 주문번호 (Order의 merchantId와 동일)

    @Column(unique = true)
    private String paymentKey;      // PG사 결제 키

    private String orderId;         // PG사 주문 ID

    @Column(nullable = false)
    private Integer amount;         // 결제 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;   // 결제 상태

    private String method;          // 결제 수단 (카드, 계좌이체 등)

    private LocalDateTime approvedAt; // 승인 시간

    private String receiptUrl;      // 영수증 URL

    private String failReason;      // 실패 사유

    private String cancelReason;    // 취소 사유

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;            // 연결된 주문

    @Builder
    public Payment(String merchantId, String paymentKey, String orderId,
                   Integer amount, PaymentStatus status, String method, Order order) {
        this.merchantId = merchantId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.method = method;
        this.order = order;
    }

    // 비즈니스 로직
    public void approve(String paymentKey, String receiptUrl) {
        this.paymentKey = paymentKey;
        this.status = PaymentStatus.DONE;
        this.approvedAt = LocalDateTime.now();
        this.receiptUrl = receiptUrl;
    }

    public void fail(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failReason = reason;
    }

    public void cancel(String reason) {
        if (this.status != PaymentStatus.DONE) {
            throw new IllegalStateException("완료된 결제만 취소할 수 있습니다.");
        }
        this.status = PaymentStatus.CANCELED;
        this.cancelReason = reason;
    }

    public boolean isDone() {
        return this.status == PaymentStatus.DONE;
    }
}
