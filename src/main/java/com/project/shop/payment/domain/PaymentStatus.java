package com.project.shop.payment.domain;

public enum PaymentStatus {
    READY,              // 결제 대기
    IN_PROGRESS,        // 결제 진행 중
    DONE,               // 결제 완료
    CANCELED,           // 결제 취소
    FAILED              // 결제 실패
}
