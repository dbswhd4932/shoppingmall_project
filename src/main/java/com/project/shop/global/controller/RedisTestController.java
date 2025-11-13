package com.project.shop.global.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis 연결 테스트 컨트롤러
 *
 * 학습 목적:
 * 1. RedisTemplate 사용법 익히기
 * 2. Redis 명령어와 Java 메서드 매핑 이해
 * 3. 실제 장바구니 구현 전 테스트
 */
@Slf4j
@RestController
@RequestMapping("/api/redis/test")
@RequiredArgsConstructor
public class RedisTestController {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 1. String 타입 테스트 (SET, GET)
     *
     * Redis 명령어:
     * SET test:key "Hello Redis"
     * GET test:key
     */
    @GetMapping("/string")
    public Map<String, Object> testString() {
        String key = "test:string";
        String value = "Hello Redis!";

        // 저장 (SET)
        redisTemplate.opsForValue().set(key, value);
        log.info("[Redis Test] String 저장 완료: {} = {}", key, value);

        // 조회 (GET)
        Object result = redisTemplate.opsForValue().get(key);
        log.info("[Redis Test] String 조회 완료: {} = {}", key, result);

        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("value", result);
        response.put("type", "String");
        return response;
    }

    /**
     * 2. TTL(Time To Live) 테스트
     *
     * Redis 명령어:
     * SET test:ttl "30초 후 삭제" EX 30
     * TTL test:ttl
     */
    @GetMapping("/ttl")
    public Map<String, Object> testTTL() {
        String key = "test:ttl";
        String value = "30초 후 자동 삭제됩니다";

        // TTL 30초로 저장
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(30));
        log.info("[Redis Test] TTL 설정 완료: {} (30초)", key);

        // TTL 확인
        Long ttl = redisTemplate.getExpire(key);
        log.info("[Redis Test] 남은 시간: {}초", ttl);

        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("value", value);
        response.put("ttl_seconds", ttl);
        response.put("message", "30초 후 자동 삭제됩니다");
        return response;
    }

    /**
     * 3. Hash 타입 테스트 (장바구니 구조와 동일!)
     *
     * Redis 명령어:
     * HSET cart:user:123 goods:1:option:0 "2"
     * HSET cart:user:123 goods:2:option:null "1"
     * HGETALL cart:user:123
     */
    @GetMapping("/hash")
    public Map<String, Object> testHash() {
        String key = "test:cart:user:999";

        // 장바구니 데이터 구조 (실제 장바구니와 동일)
        // field: "goods:{goodsId}:option:{optionId}"
        // value: 수량
        redisTemplate.opsForHash().put(key, "goods:1:option:0", "2");
        redisTemplate.opsForHash().put(key, "goods:2:option:null", "1");
        redisTemplate.opsForHash().put(key, "goods:3:option:1", "5");

        log.info("[Redis Test] Hash 저장 완료: {}", key);

        // 모든 데이터 조회 (HGETALL)
        Map<Object, Object> cart = redisTemplate.opsForHash().entries(key);
        log.info("[Redis Test] Hash 조회 완료: {}", cart);

        // TTL 30일 설정 (실제 장바구니도 30일 후 자동 삭제)
        redisTemplate.expire(key, Duration.ofDays(30));

        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("cart_items", cart);
        response.put("item_count", cart.size());
        response.put("ttl_days", 30);
        return response;
    }

    /**
     * 4. Hash 수량 증가 테스트 (원자적 연산)
     *
     * Redis 명령어:
     * HINCRBY cart:user:123 goods:1:option:0 3
     */
    @PostMapping("/hash/increment")
    public Map<String, Object> testHashIncrement(@RequestParam(defaultValue = "1") Long amount) {
        String key = "test:cart:user:999";
        String field = "goods:1:option:0";

        // 수량 증가 (원자적 연산 - 동시성 문제 없음!)
        Long newAmount = redisTemplate.opsForHash().increment(key, field, amount);
        log.info("[Redis Test] Hash 수량 증가: {} = {}", field, newAmount);

        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("field", field);
        response.put("new_amount", newAmount);
        response.put("increment_by", amount);
        response.put("message", "동시성 문제 없이 안전하게 증가!");
        return response;
    }

    /**
     * 5. Hash 특정 필드 삭제 테스트
     *
     * Redis 명령어:
     * HDEL cart:user:123 goods:2:option:null
     */
    @DeleteMapping("/hash/{field}")
    public Map<String, Object> testHashDelete(@PathVariable String field) {
        String key = "test:cart:user:999";

        // 삭제 전 값 확인
        Object oldValue = redisTemplate.opsForHash().get(key, field);

        // 필드 삭제
        Long deleted = redisTemplate.opsForHash().delete(key, field);
        log.info("[Redis Test] Hash 필드 삭제: {} - deleted count: {}", field, deleted);

        Map<String, Object> response = new HashMap<>();
        response.put("key", key);
        response.put("deleted_field", field);
        response.put("old_value", oldValue);
        response.put("deleted_count", deleted);
        return response;
    }

    /**
     * 6. 모든 테스트 데이터 삭제
     *
     * Redis 명령어:
     * DEL test:*
     */
    @DeleteMapping("/cleanup")
    public Map<String, Object> cleanup() {
        // 테스트 키들 삭제
        redisTemplate.delete("test:string");
        redisTemplate.delete("test:ttl");
        redisTemplate.delete("test:cart:user:999");

        log.info("[Redis Test] 테스트 데이터 정리 완료");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "모든 테스트 데이터가 삭제되었습니다");
        return response;
    }

    /**
     * 7. Redis 연결 상태 확인
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        try {
            // PING 명령어 실행
            String key = "test:ping";
            redisTemplate.opsForValue().set(key, "pong");
            Object result = redisTemplate.opsForValue().get(key);
            redisTemplate.delete(key);

            log.info("[Redis Test] 연결 테스트 성공");

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Redis 연결 정상");
            response.put("result", result);
            return response;
        } catch (Exception e) {
            log.error("[Redis Test] 연결 테스트 실패", e);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Redis 연결 실패: " + e.getMessage());
            return response;
        }
    }
}
