package com.project.shop.payment.controller;

import com.project.shop.payment.dto.PaymentCancelRequest;
import com.project.shop.payment.dto.PaymentConfirmRequest;
import com.project.shop.payment.dto.PaymentRequest;
import com.project.shop.payment.dto.PaymentResponse;
import com.project.shop.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 결제 API 컨트롤러
 * TossPayments 연동 예시
 *
 * 결제 플로우:
 * 1. 프론트엔드: 주문 정보 입력 → POST /api/payments/prepare (결제 준비)
 * 2. 프론트엔드: TossPayments SDK로 결제창 호출
 * 3. 사용자: 결제 진행 (카드 정보 입력 등)
 * 4. 프론트엔드: 결제 성공 시 → POST /api/payments/confirm (결제 승인)
 * 5. 백엔드: TossPayments API로 최종 승인 요청
 * 6. 프론트엔드: 결제 완료 페이지 이동
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@Tag(name = "결제", description = "결제 관련 API")
public class PaymentController {

    private final PaymentService paymentService;

    // @Qualifier로 TossPaymentService 빈을 명시적으로 주입
    public PaymentController(@Qualifier("tossPaymentService") PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * 결제 준비
     * 주문 생성 시 자동으로 호출되거나, 별도로 결제 준비 요청 가능
     */
    @Operation(summary = "결제 준비", description = "결제를 준비하고 결제 정보를 생성합니다.")
    @PostMapping("/prepare")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    public ResponseEntity<PaymentResponse> preparePayment(@Valid @RequestBody PaymentRequest request) {
        log.info("결제 준비 요청 - merchantId: {}, amount: {}", request.getMerchantId(), request.getAmount());
        PaymentResponse response = paymentService.requestPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 결제 승인 (최종 결제)
     * 사용자가 TossPayments 결제창에서 결제 완료 후 호출
     */
    @Operation(summary = "결제 승인", description = "TossPayments에서 발급받은 결제키로 최종 승인합니다.")
    @PostMapping("/confirm")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    public ResponseEntity<PaymentResponse> confirmPayment(@Valid @RequestBody PaymentConfirmRequest request) {
        log.info("결제 승인 요청 - paymentKey: {}, orderId: {}", request.getPaymentKey(), request.getOrderId());
        PaymentResponse response = paymentService.confirmPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 취소
     */
    @Operation(summary = "결제 취소", description = "완료된 결제를 취소합니다.")
    @PostMapping("/cancel")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    public ResponseEntity<PaymentResponse> cancelPayment(@Valid @RequestBody PaymentCancelRequest request) {
        log.info("결제 취소 요청 - paymentKey: {}", request.getPaymentKey());
        PaymentResponse response = paymentService.cancelPayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 결제 상태 조회
     */
    @Operation(summary = "결제 상태 조회", description = "결제 키로 결제 상태를 조회합니다.")
    @GetMapping("/{paymentKey}")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_SELLER','ROLE_ADMIN')")
    public ResponseEntity<PaymentResponse> getPaymentStatus(@PathVariable String paymentKey) {
        log.info("결제 상태 조회 - paymentKey: {}", paymentKey);
        PaymentResponse response = paymentService.getPaymentStatus(paymentKey);
        return ResponseEntity.ok(response);
    }
}
