package com.project.shop.payment.service;

import com.project.shop.payment.domain.Payment;
import com.project.shop.payment.domain.PaymentStatus;
import com.project.shop.payment.dto.PaymentCancelRequest;
import com.project.shop.payment.dto.PaymentConfirmRequest;
import com.project.shop.payment.dto.PaymentRequest;
import com.project.shop.payment.dto.PaymentResponse;
import com.project.shop.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * TossPayments API를 사용한 결제 서비스 구현
 * 실제 운영 시 application.yml에 설정 추가 필요:
 * payment:
 *   toss:
 *     secret-key: {발급받은 시크릿 키}
 *     success-url: http://localhost:3000/payment/success
 *     fail-url: http://localhost:3000/payment/fail
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TossPaymentService implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;

    @Value("${payment.toss.secret-key:test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R}")
    private String secretKey;

    @Value("${payment.toss.api-url:https://api.tosspayments.com/v1/payments}")
    private String apiUrl;

    /**
     * 주문 생성 시 > 실제 결제 안됨
     * @param request 결제 요청 정보
     * @return
     */
    @Override
    public PaymentResponse requestPayment(PaymentRequest request) {
        log.info("결제 요청 - merchantId: {}, amount: {}", request.getMerchantId(), request.getAmount());

        // Payment 엔티티 생성 (READY 상태)
        Payment payment = Payment.builder()
                .merchantId(request.getMerchantId())
                .orderId(request.getMerchantId())
                .amount(request.getAmount())
                .status(PaymentStatus.READY)
                .method(request.getPaymentMethod())
                .build();

        paymentRepository.save(payment);

        // TossPayments는 클라이언트에서 직접 SDK로 결제 요청하므로
        // 서버에서는 결제 정보만 저장하고 응답
        return PaymentResponse.builder()
                .merchantId(request.getMerchantId())
                .orderId(payment.getOrderId())
                .amount(request.getAmount())
                .status(PaymentStatus.READY.name())
                .method(request.getPaymentMethod())
                .build();
    }

    /**
     * 사용자가 결제창에서 결제 후 > 실제로 돈 빠짐
     * @param request 결제 승인 요청
     * @return
     */
    @Override
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        log.info("결제 승인 요청 - paymentKey: {}, orderId: {}, amount: {}",
                request.getPaymentKey(), request.getOrderId(), request.getAmount());

        try {
            // TossPayments API 호출
            HttpHeaders headers = createHeaders();
            Map<String, Object> body = new HashMap<>();
            body.put("paymentKey", request.getPaymentKey());
            body.put("orderId", request.getOrderId());
            body.put("amount", request.getAmount());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "/confirm",
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();

                // Payment 엔티티 업데이트
                Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

                payment.approve(
                        request.getPaymentKey(),
                        (String) responseBody.get("receipt")
                );

                log.info("결제 승인 완료 - paymentKey: {}", request.getPaymentKey());

                return buildPaymentResponse(payment, responseBody);
            } else {
                throw new RuntimeException("결제 승인 실패");
            }

        } catch (Exception e) {
            log.error("결제 승인 중 오류 발생", e);

            // Payment 상태를 FAILED로 업데이트
            Payment payment = paymentRepository.findByOrderId(request.getOrderId())
                    .orElse(null);
            if (payment != null) {
                payment.fail(e.getMessage());
            }

            throw new RuntimeException("결제 승인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse cancelPayment(PaymentCancelRequest request) {
        log.info("결제 취소 요청 - paymentKey: {}, reason: {}",
                request.getPaymentKey(), request.getCancelReason());

        try {
            // Payment 조회
            Payment payment = paymentRepository.findByPaymentKey(request.getPaymentKey())
                    .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

            // TossPayments API 호출
            HttpHeaders headers = createHeaders();
            Map<String, Object> body = new HashMap<>();
            body.put("cancelReason", request.getCancelReason());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "/" + request.getPaymentKey() + "/cancel",
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                // Payment 상태 업데이트
                payment.cancel(request.getCancelReason());

                log.info("결제 취소 완료 - paymentKey: {}", request.getPaymentKey());

                return PaymentResponse.builder()
                        .merchantId(payment.getMerchantId())
                        .paymentKey(payment.getPaymentKey())
                        .orderId(payment.getOrderId())
                        .amount(payment.getAmount())
                        .status(PaymentStatus.CANCELED.name())
                        .build();
            } else {
                throw new RuntimeException("결제 취소 실패");
            }

        } catch (Exception e) {
            log.error("결제 취소 중 오류 발생", e);
            throw new RuntimeException("결제 취소 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentStatus(String paymentKey) {
        log.info("결제 상태 조회 - paymentKey: {}", paymentKey);

        try {
            // TossPayments API 호출
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl + "/" + paymentKey,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();

                // DB의 Payment 정보도 함께 조회
                Payment payment = paymentRepository.findByPaymentKey(paymentKey)
                        .orElse(null);

                return buildPaymentResponse(payment, responseBody);
            } else {
                throw new RuntimeException("결제 상태 조회 실패");
            }

        } catch (Exception e) {
            log.error("결제 상태 조회 중 오류 발생", e);
            throw new RuntimeException("결제 상태 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * TossPayments API 호출을 위한 헤더 생성
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Basic Auth: secretKey를 Base64 인코딩
        String auth = secretKey + ":";
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encodedAuth);

        return headers;
    }

    /**
     * TossPayments API 응답을 PaymentResponse로 변환
     */
    private PaymentResponse buildPaymentResponse(Payment payment, Map<String, Object> apiResponse) {
        return PaymentResponse.builder()
                .merchantId(payment != null ? payment.getMerchantId() : null)
                .paymentKey((String) apiResponse.get("paymentKey"))
                .orderId((String) apiResponse.get("orderId"))
                .amount((Integer) apiResponse.get("totalAmount"))
                .status((String) apiResponse.get("status"))
                .method((String) apiResponse.get("method"))
                .approvedAt(apiResponse.get("approvedAt") != null ?
                        LocalDateTime.parse((String) apiResponse.get("approvedAt")) : null)
                .receiptUrl((String) apiResponse.getOrDefault("receipt", null))
                .build();
    }
}
