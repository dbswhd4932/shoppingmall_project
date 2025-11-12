package com.project.shop.order.scheduler;

import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderStatus;
import com.project.shop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusScheduler {

    private final OrderRepository orderRepository;

    /**
     * 매일 16:00에 주문완료 상태의 주문을 배송중으로 변경
     * 주말(토요일, 일요일)은 제외
     */
    @Scheduled(cron = "0 0 16 * * MON-FRI")  // 월-금 16:00
    @Transactional
    public void updateOrderStatusToDelivery() {
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek dayOfWeek = now.getDayOfWeek();

        // 주말인 경우 실행하지 않음 (cron에서 이미 처리되지만 추가 검증)
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            log.info("주말이므로 주문 상태 업데이트를 건너뜁니다.");
            return;
        }

        // 주문완료 상태인 주문 조회
        List<Order> orders = orderRepository.findByOrderStatus(OrderStatus.ORDER);

        if (orders.isEmpty()) {
            log.info("배송 대기중인 주문이 없습니다.");
            return;
        }

        // 주문 상태를 배송중으로 변경
        int updatedCount = 0;
        for (Order order : orders) {
            order.orderStatusChangeDelivery();
            updatedCount++;
        }

        log.info("총 {}개의 주문이 배송중으로 변경되었습니다. (실행 시간: {})", updatedCount, now);
    }
}
