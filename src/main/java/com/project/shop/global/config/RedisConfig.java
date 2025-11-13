package com.project.shop.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스
 *
 * 학습 포인트:
 * 1. RedisConnectionFactory: Redis 서버와의 연결을 관리
 * 2. RedisTemplate: Redis 데이터 조작을 위한 핵심 클래스
 * 3. Serializer: Java 객체 <-> Redis 저장 형식 변환
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host:localhost}")
    private String host;

    @Value("${spring.redis.port:6379}")
    private int port;

    /**
     * Redis 연결 팩토리 생성
     *
     * Lettuce를 사용한 연결 관리
     * - Thread-safe: 멀티스레드 환경에서 안전
     * - Connection Pooling: 자동으로 연결 풀 관리
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * Redis 전용 ObjectMapper 커스터마이징
     *
     * Jackson을 사용한 JSON 직렬화 설정
     * - JavaTimeModule: LocalDateTime, LocalDate 등 Java 8 시간 타입 지원
     * - 전역 ObjectMapper와 충돌하지 않도록 별도 빈으로 관리
     */
    @Bean(name = "redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Java 8 날짜/시간 타입 지원
        mapper.registerModule(new JavaTimeModule());

        // 날짜를 timestamp가 아닌 ISO-8601 문자열로 직렬화
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Redis 장바구니에는 단순 DTO(CartItem)만 저장하므로
        // 다형성 타입 정보(activateDefaultTyping)는 불필요
        // 제거함으로써 전역 ObjectMapper와의 충돌 방지

        return mapper;
    }

    /**
     * RedisTemplate 빈 생성 (핵심!)
     *
     * Key-Value 직렬화 전략:
     * - Key: StringRedisSerializer
     *   → "cart:user:123" 같은 문자열 키
     *
     * - Value: GenericJackson2JsonRedisSerializer
     *   → Java 객체를 JSON으로 저장
     *   → 예: {"goodsId":1, "amount":2} 형태로 Redis에 저장
     *
     * Hash 구조 직렬화:
     * - HashKey: String (필드명)
     * - HashValue: JSON (필드값)
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            ObjectMapper redisObjectMapper) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // JSON 직렬화 설정 (Redis 전용 ObjectMapper 사용)
        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        // Key는 String 형태로 저장 (예: "cart:user:123")
        template.setKeySerializer(new StringRedisSerializer());

        // Value는 JSON 형태로 저장 (예: {"goodsId": 1, "amount": 2})
        template.setValueSerializer(serializer);

        // Hash 구조의 Key도 String 형태
        template.setHashKeySerializer(new StringRedisSerializer());

        // Hash 구조의 Value도 JSON 형태
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
