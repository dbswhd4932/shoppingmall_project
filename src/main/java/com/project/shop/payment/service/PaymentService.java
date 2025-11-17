package com.project.shop.payment.service;

import com.project.shop.payment.dto.PaymentCancelRequest;
import com.project.shop.payment.dto.PaymentConfirmRequest;
import com.project.shop.payment.dto.PaymentRequest;
import com.project.shop.payment.dto.PaymentResponse;

/**
 * 결제 서비스 인터페이스
 * 다양한 PG사를 지원하기 위한 추상화
 */
public interface PaymentService {

    /**
     * 결제 준비 (결제 요청)
     * @param request 결제 요청 정보
     * @return 결제 응답 (결제 페이지 URL 등)
     */
    PaymentResponse requestPayment(PaymentRequest request);

    /**
     * 결제 승인 (사용자가 결제 완료 후 호출)
     * @param request 결제 승인 요청
     * @return 결제 승인 결과
     */
    PaymentResponse confirmPayment(PaymentConfirmRequest request);

    /**
     * 결제 취소
     * @param request 결제 취소 요청
     * @return 취소 결과
     */
    PaymentResponse cancelPayment(PaymentCancelRequest request);

    /**
     * 결제 상태 조회
     * @param paymentKey PG사 결제 키
     * @return 결제 정보
     */
    PaymentResponse getPaymentStatus(String paymentKey);
}
