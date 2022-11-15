package com.project.shop.order.repository;

import com.project.shop.order.domain.entity.Pay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayRepository extends JpaRepository<Pay, Long> {
}
