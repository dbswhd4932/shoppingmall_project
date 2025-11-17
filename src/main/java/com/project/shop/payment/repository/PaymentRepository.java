package com.project.shop.payment.repository;

import com.project.shop.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByMerchantId(UUID merchantId);

    Optional<Payment> findByPaymentKey(String paymentKey);

    Optional<Payment> findByOrderId(String orderId);
}
