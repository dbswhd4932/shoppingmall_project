package com.project.shop.order.repository;

import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findAllByOrder(Order order);
}
