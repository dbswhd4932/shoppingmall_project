package com.project.shop.global.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Refresh Token 관리 서비스
 *
 * Redis를 사용하여 Refresh Token 저장/조회/삭제/연장
 *
 * 학습 포인트:
 * 1. Redis TTL을 활용한 자동 만료 처리
 * 2. Sliding Session: 토큰 사용 시 TTL 연장
 * 3. 로그아웃 시 즉시 무효화
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    // Redis Key Prefix
    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    // Refresh Token 유효기간: 7일 (초 단위)
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60; // 604800초

    /**
     * Refresh Token 저장
     *
     * @param loginId 사용자 로그인 ID
     * @param refreshToken Refresh Token 문자열
     */
    public void saveRefreshToken(String loginId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + loginId;
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        log.info("✅ Refresh Token saved for loginId: {}, TTL: {} seconds", loginId, REFRESH_TOKEN_EXPIRE_TIME);
    }

    /**
     * Refresh Token 조회
     *
     * @param loginId 사용자 로그인 ID
     * @return Refresh Token 문자열 (없으면 null)
     */
    public String getRefreshToken(String loginId) {
        String key = REFRESH_TOKEN_PREFIX + loginId;
        String token = redisTemplate.opsForValue().get(key);

        if (token == null) {
            log.warn("⚠️ Refresh Token not found for loginId: {}", loginId);
        }

        return token;
    }

    /**
     * Refresh Token 삭제 (로그아웃)
     *
     * @param loginId 사용자 로그인 ID
     */
    public void deleteRefreshToken(String loginId) {
        String key = REFRESH_TOKEN_PREFIX + loginId;
        Boolean deleted = redisTemplate.delete(key);

        if (Boolean.TRUE.equals(deleted)) {
            log.info("🗑️ Refresh Token deleted for loginId: {}", loginId);
        } else {
            log.warn("⚠️ Refresh Token deletion failed for loginId: {}", loginId);
        }
    }

    /**
     * Refresh Token TTL 연장 (Sliding Session)
     *
     * 토큰 재발급 시 TTL을 다시 7일로 연장
     * → 활성 사용자는 자동으로 세션 유지
     * → 비활성 사용자는 7일 후 자동 로그아웃
     *
     * @param loginId 사용자 로그인 ID
     * @param refreshToken Refresh Token 문자열
     */
    public void extendRefreshToken(String loginId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + loginId;
        redisTemplate.opsForValue().set(key, refreshToken, REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        log.info("🔄 Refresh Token TTL extended for loginId: {}", loginId);
    }

    /**
     * Refresh Token 존재 여부 확인
     *
     * @param loginId 사용자 로그인 ID
     * @return 존재 여부
     */
    public boolean hasRefreshToken(String loginId) {
        String key = REFRESH_TOKEN_PREFIX + loginId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 남은 TTL 조회 (초 단위)
     *
     * @param loginId 사용자 로그인 ID
     * @return 남은 시간 (초), 없으면 -2
     */
    public Long getTimeToLive(String loginId) {
        String key = REFRESH_TOKEN_PREFIX + loginId;
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }
}
