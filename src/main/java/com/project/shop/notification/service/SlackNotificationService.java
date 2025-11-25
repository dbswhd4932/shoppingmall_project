package com.project.shop.notification.service;

import com.project.shop.notification.dto.SlackMessage;
import com.project.shop.order.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Slack 알림 서비스
 *
 * RabbitMQ Consumer에서 주문 이벤트를 받아 Slack으로 알림 전송
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "slack.webhook.enabled", havingValue = "true", matchIfMissing = false)
public class SlackNotificationService {

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 주문 생성 알림을 Slack으로 전송
     *
     * @param event 주문 생성 이벤트
     */
    public void sendOrderNotification(OrderCreatedEvent event) {
        try {
            log.info("[Slack] 주문 알림 전송 시작: orderId={}", event.getOrderId());

            SlackMessage message = buildOrderMessage(event);
            sendMessage(message);

            log.info("[Slack] 주문 알림 전송 성공: orderId={}", event.getOrderId());

        } catch (Exception e) {
            log.error("[Slack] 주문 알림 전송 실패: orderId={}, error={}",
                    event.getOrderId(), e.getMessage(), e);
            // Slack 전송 실패해도 예외를 던지지 않음 (주문 처리는 성공했으므로)
        }
    }

    /**
     * Slack 메시지 빌드 (간단한 텍스트 형태)
     *
     * @param event 주문 생성 이벤트
     * @return SlackMessage
     */
    private SlackMessage buildOrderMessage(OrderCreatedEvent event) {
        // 금액 포맷팅 (예: 50000 -> ₩50,000)
        String formattedPrice = formatPrice(event.getTotalPrice());

        // 날짜 포맷팅
        String formattedDate = event.getCreatedAt().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );

        // 메시지 텍스트 구성
        StringBuilder messageText = new StringBuilder();
        messageText.append("🛒 *새로운 주문이 접수되었습니다!*\n\n");
        messageText.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        messageText.append("📋 *주문 정보*\n");
        messageText.append("• 주문 ID: `").append(event.getOrderId()).append("`\n");
        messageText.append("• 주문 번호: `").append(event.getMerchantId()).append("`\n");
        messageText.append("• 주문자: `").append(event.getMemberLoginId()).append("`\n");
        messageText.append("• 이메일: `").append(event.getMemberEmail()).append("`\n");
        messageText.append("• 주문 금액: *").append(formattedPrice).append("*\n");
        messageText.append("• 주문 상태: `").append(event.getOrderStatus()).append("`\n");
        messageText.append("• 주문 시간: `").append(formattedDate).append("`\n");
        messageText.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // 간단한 텍스트 메시지로 전송
        return SlackMessage.builder()
                .text(messageText.toString())
                .build();
    }

    /**
     * Slack Webhook으로 메시지 전송
     *
     * @param message Slack 메시지
     */
    private void sendMessage(SlackMessage message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SlackMessage> request = new HttpEntity<>(message, headers);

        restTemplate.postForEntity(webhookUrl, request, String.class);
    }

    /**
     * 금액 포맷팅
     *
     * @param price 가격
     * @return 포맷팅된 가격 문자열 (예: ₩50,000)
     */
    private String formatPrice(Integer price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.KOREA);
        return "₩" + formatter.format(price);
    }
}