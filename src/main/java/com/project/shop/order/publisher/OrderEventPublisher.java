package com.project.shop.order.publisher;

import com.project.shop.global.config.RabbitMQConfig;
import com.project.shop.order.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 주문 이벤트 발행 클래스
 * <p>
 * 주문 생성 시 RabbitMQ에 이벤트를 발행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 주문 생성 이벤트 발행
     *
     * @param event 주문 생성 이벤트
     */
    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("[RabbitMQ] 주문 생성 이벤트 발행 시작: orderId={}, merchantId={}",
                    event.getOrderId(), event.getMerchantId());

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,              // Exchange
                    RabbitMQConfig.ORDER_NOTIFICATION_ROUTING_KEY,  // Routing Key
                    event,                                       // Message (자동으로 JSON 변환됨)
                    m -> {
                        m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT); // 메시지 영속화(메시지 유실 대비)
                        return m;
                    }
            );

            log.info("[RabbitMQ] 주문 생성 이벤트 발행 성공: orderId={}, memberEmail={}",
                    event.getOrderId(), event.getMemberEmail());

        } catch (Exception e) {
            log.error("[RabbitMQ] 주문 생성 이벤트 발행 실패: orderId={}, error={}",
                    event.getOrderId(), e.getMessage(), e);
            // 이벤트 발행 실패해도 주문은 성공했으므로 예외를 던지지 않음
            // 필요시 재시도 로직 추가 가능
        }
    }
}
