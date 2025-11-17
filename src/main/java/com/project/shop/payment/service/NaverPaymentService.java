package com.project.shop.payment.service;

import com.project.shop.payment.dto.PaymentCancelRequest;
import com.project.shop.payment.dto.PaymentConfirmRequest;
import com.project.shop.payment.dto.PaymentRequest;
import com.project.shop.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  인터페이스를 이용해서 여러 payment 추가 가능
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NaverPaymentService implements PaymentService {

    @Override
    public PaymentResponse requestPayment(PaymentRequest request) {
        return null;
    }

    @Override
    public PaymentResponse confirmPayment(PaymentConfirmRequest request) {
        return null;
    }

    @Override
    public PaymentResponse cancelPayment(PaymentCancelRequest request) {
        return null;
    }

    @Override
    public PaymentResponse getPaymentStatus(String paymentKey) {
        return null;
    }
}
