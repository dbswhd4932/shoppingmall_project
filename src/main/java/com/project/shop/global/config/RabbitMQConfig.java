package com.project.shop.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 설정 클래스
 *
 * - Queue: 메시지를 저장하는 큐
 * - Exchange: 메시지를 라우팅하는 교환기
 * - Binding: Queue와 Exchange를 연결하는 바인딩
 * - RabbitTemplate: 메시지 발행을 위한 템플릿
 */
@Configuration
public class RabbitMQConfig {

    // Queue 이름 상수
    public static final String ORDER_NOTIFICATION_QUEUE = "order.notification.queue";

    // Exchange 이름 상수
    public static final String ORDER_EXCHANGE = "order.exchange";

    // Routing Key 상수
    public static final String ORDER_NOTIFICATION_ROUTING_KEY = "order.notification";

    /**
     * 주문 알림 큐 생성
     *
     * durable = true: 서버 재시작 시에도 큐 유지 (필수)
     */
    @Bean
    public Queue orderNotificationQueue() {
        return new Queue(ORDER_NOTIFICATION_QUEUE, true);
    }

    /**
     * 주문 Exchange 생성
     *
     * DirectExchange: Routing Key가 정확히 일치하는 Queue로만 메시지 전달
     */
    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange(ORDER_EXCHANGE);
    }

    /**
     * Queue와 Exchange 바인딩
     *
     * orderNotificationQueue를 orderExchange에 바인딩
     * Routing Key: order.notification
     */
    @Bean
    public Binding orderNotificationBinding(Queue orderNotificationQueue, DirectExchange orderExchange) {
        return BindingBuilder
                .bind(orderNotificationQueue)
                .to(orderExchange)
                .with(ORDER_NOTIFICATION_ROUTING_KEY);
    }

    /**
     * 메시지 컨버터 설정
     *
     * Jackson2JsonMessageConverter를 사용하여 객체 <-> JSON 자동 변환
     * LocalDateTime 등 Java 8 날짜/시간 API 지원을 위해 JavaTimeModule 추가
     */
    @Bean
    public MessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    /**
     * RabbitTemplate 설정
     *
     * 메시지를 발행할 때 사용하는 템플릿
     * JSON 컨버터를 설정하여 객체를 자동으로 JSON으로 변환
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}
