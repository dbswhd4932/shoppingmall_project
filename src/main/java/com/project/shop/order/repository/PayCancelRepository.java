package com.project.shop.order.repository;

import com.project.shop.order.domain.PayCancel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayCancelRepository extends JpaRepository<PayCancel, Long> {
}
