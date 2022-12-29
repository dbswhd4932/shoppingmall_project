package com.project.shop.order.repository;

import com.project.shop.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByMemberId(Long memberId);
    Optional<Order> findByMerchantId(String merchantId);

}
