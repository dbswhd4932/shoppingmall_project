package com.project.shop.order.consumer;

import com.project.shop.global.config.RabbitMQConfig;
import com.project.shop.order.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 주문 알림 Consumer
 *
 * RabbitMQ에서 주문 생성 이벤트를 받아서 처리
 * - 이메일 발송
 * - 알림톡 발송
 * - 로그 기록
 * 등의 비동기 작업 수행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderNotificationConsumer {

    /**
     * 주문 생성 이벤트 리스너
     *
     * @param event 주문 생성 이벤트
     *
     * @RabbitListener: RabbitMQ 큐를 리스닝하는 어노테이션
     * - queues: 리스닝할 큐 이름
     * - 메시지가 큐에 도착하면 자동으로 이 메서드가 호출됨
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_NOTIFICATION_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("=".repeat(80));
            log.info("[RabbitMQ Consumer] 주문 생성 이벤트 수신 시작");
            log.info("주문 ID: {}", event.getOrderId());
            log.info("주문 번호: {}", event.getMerchantId());
            log.info("회원 ID: {} ({})", event.getMemberId(), event.getMemberLoginId());
            log.info("회원 이메일: {}", event.getMemberEmail());
            log.info("총 주문 금액: ₩{}", String.format("%,d", event.getTotalPrice()));
            log.info("주문 상태: {}", event.getOrderStatus());
            log.info("주문 생성 시간: {}", event.getCreatedAt());
            log.info("이벤트 발생 시간: {}", event.getEventTime());
            log.info("=".repeat(80));

            // ========================================
            // 여기서 실제 알림 처리 로직 수행
            // ========================================

            // 1. 이메일 발송 (미구현 - 로그만 출력)
            sendEmailNotification(event);

            // 2. 알림톡 발송 (미구현 - 로그만 출력)
            sendKakaoNotification(event);

            // 3. 관리자에게 알림 (미구현 - 로그만 출력)
            notifyAdmin(event);

            log.info("[RabbitMQ Consumer] 주문 생성 이벤트 처리 완료: orderId={}", event.getOrderId());

        } catch (Exception e) {
            log.error("[RabbitMQ Consumer] 주문 생성 이벤트 처리 실패: orderId={}, error={}",
                    event.getOrderId(), e.getMessage(), e);
            // 실패 시 재시도 로직 추가 가능 (DLQ - Dead Letter Queue 활용)
        }
    }

    /**
     * 이메일 발송 (미구현 - 로그만 출력)
     */
    private void sendEmailNotification(OrderCreatedEvent event) {
        log.info("[Email] 주문 확인 이메일 발송 (구현 필요)");
        log.info("  - 수신자: {}", event.getMemberEmail());
        log.info("  - 제목: 주문이 완료되었습니다 (주문번호: {})", event.getMerchantId());
        log.info("  - 내용: 총 {}원의 주문이 성공적으로 접수되었습니다.", String.format("%,d", event.getTotalPrice()));

        // 실제 이메일 발송 로직 추가
        // emailService.sendOrderConfirmation(event);
    }

    /**
     * 카카오 알림톡 발송 (미구현 - 로그만 출력)
     */
    private void sendKakaoNotification(OrderCreatedEvent event) {
        log.info("[Kakao] 주문 확인 알림톡 발송 (구현 필요)");
        log.info("  - 수신자: {}", event.getMemberLoginId());
        log.info("  - 메시지: 주문번호 {} - 총 {}원",
                event.getMerchantId(), String.format("%,d", event.getTotalPrice()));

        // 실제 알림톡 발송 로직 추가
        // kakaoService.sendOrderNotification(event);
    }

    /**
     * 관리자에게 알림 (미구현 - 로그만 출력)
     */
    private void notifyAdmin(OrderCreatedEvent event) {
        log.info("[Admin] 관리자에게 신규 주문 알림 (구현 필요)");
        log.info("  - 주문 ID: {}", event.getOrderId());
        log.info("  - 주문 금액: ₩{}", String.format("%,d", event.getTotalPrice()));

        // 실제 관리자 알림 로직 추가
        // adminNotificationService.notifyNewOrder(event);
    }
}
