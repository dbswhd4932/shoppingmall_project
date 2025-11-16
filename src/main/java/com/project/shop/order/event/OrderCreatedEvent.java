package com.project.shop.order.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 주문 생성 이벤트 DTO
 *
 * RabbitMQ를 통해 전달되는 주문 생성 이벤트 정보
 * Serializable을 구현하여 직렬화 가능하도록 설정
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 주문 ID
     */
    private Long orderId;

    /**
     * 주문번호 (UUID 문자열)
     */
    private String merchantId;

    /**
     * 회원 ID
     */
    private Long memberId;

    /**
     * 회원 로그인 ID
     */
    private String memberLoginId;

    /**
     * 회원 이메일
     */
    private String memberEmail;

    /**
     * 총 주문 금액
     */
    private Integer totalPrice;

    /**
     * 주문 상태
     */
    private String orderStatus;

    /**
     * 주문 생성 시간
     */
    private LocalDateTime createdAt;

    /**
     * 이벤트 발생 시간
     */
    @Builder.Default
    private LocalDateTime eventTime = LocalDateTime.now();
}
