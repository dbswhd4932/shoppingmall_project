package com.project.shop.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Options;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartResponse;
import com.project.shop.member.domain.CartItem;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.shop.global.error.ErrorCode.*;

/**
 * Redis 기반 장바구니 서비스
 *
 * 학습 포인트:
 * 1. Redis Hash 구조 활용
 * 2. JSON 직렬화/역직렬화
 * 3. TTL 관리
 * 4. 중복 체크
 * 5. 배치 조회 최적화
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCartService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final GoodsRepository goodsRepository;
    private final OptionRepository optionRepository;
    private final MemberRepository memberRepository;

    private static final String CART_KEY_PREFIX = "cart:user:";
    private static final int CART_TTL_DAYS = 7;

    /**
     * 1. 장바구니 추가
     *
     * Redis 명령어:
     * HEXISTS cart:user:123 goods:1:option:null
     * HSET cart:user:123 goods:1:option:null '{"amount":2,"totalPrice":30000,...}'
     * EXPIRE cart:user:123 2592000
     */
    public void addToCart(CartCreateRequest request) {
        Member member = getMember();
        String cartKey = getCartKey(member.getId());

        // 상품 조회
        Goods goods = goodsRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_GOODS));

        // 옵션 검증 및 가격 계산
        int unitPrice = calculateUnitPrice(goods, request.getOptionNumber());

        // Field 생성
        String field = getCartField(request.getGoodsId(), request.getOptionNumber());

        // 중복 체크
        if (Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(cartKey, field))) {
            throw new BusinessException(CART_IN_GOODS_DUPLICATED);
        }

        // CartItem 생성
        CartItem cartItem = new CartItem(
                request.getAmount(),
                unitPrice * request.getAmount()
        );

        // JSON 직렬화 후 Redis에 저장
        try {
            String cartItemJson = objectMapper.writeValueAsString(cartItem);
            redisTemplate.opsForHash().put(cartKey, field, cartItemJson);

            // TTL 30일 설정
            redisTemplate.expire(cartKey, Duration.ofDays(CART_TTL_DAYS));

            log.info("[Redis Cart] 장바구니 추가 성공: userId={}, goodsId={}, optionNumber={}, amount={}",
                    member.getId(), request.getGoodsId(), request.getOptionNumber(), request.getAmount());
        } catch (JsonProcessingException e) {
            log.error("[Redis Cart] JSON 직렬화 실패", e);
            throw new RuntimeException("장바구니 저장 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 2. 장바구니 조회 (페이징)
     *
     * Redis 명령어:
     * HGETALL cart:user:123
     *
     * 프로세스:
     * 1. HGETALL로 전체 장바구니 조회
     * 2. JSON → CartItem 역직렬화
     * 3. goodsId 추출 → Goods 배치 조회
     * 4. CartResponse 생성
     * 5. 애플리케이션 레벨 페이징
     */
    public Page<CartResponse> getCartItems(Pageable pageable) {
        Member member = getMember();
        String cartKey = getCartKey(member.getId());

        // 전체 장바구니 조회
        Map<Object, Object> cartData = redisTemplate.opsForHash().entries(cartKey);

        if (cartData.isEmpty()) {
            return Page.empty(pageable);
        }

        log.info("[Redis Cart] 장바구니 조회: userId={}, itemCount={}", member.getId(), cartData.size());

        // Field 파싱 + CartItem 역직렬화
        List<CartItemWithKey> cartItems = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : cartData.entrySet()) {
            String field = (String) entry.getKey();
            String cartItemJson = (String) entry.getValue();

            try {
                CartItem cartItem = objectMapper.readValue(cartItemJson, CartItem.class);

                // Field에서 goodsId, optionNumber 추출
                String[] parts = field.split(":");
                Long goodsId = Long.parseLong(parts[1]);
                Long optionNumber = "null".equals(parts[3]) ? null : Long.parseLong(parts[3]);

                cartItems.add(new CartItemWithKey(field, goodsId, optionNumber, cartItem));
            } catch (Exception e) {
                log.error("[Redis Cart] JSON 역직렬화 실패: field={}", field, e);
            }
        }

        // 최근 추가된 순서로 정렬 (addedAt 내림차순)
        cartItems.sort((a, b) -> Long.compare(b.cartItem.getAddedAt(), a.cartItem.getAddedAt()));

        // Goods 배치 조회 (images를 함께 조회하여 LazyInitializationException 방지)
        List<Long> goodsIds = cartItems.stream()
                .map(item -> item.goodsId)
                .collect(Collectors.toList());

        List<Goods> goodsList = goodsRepository.findAllByIdWithImages(goodsIds);

        // CartResponse 생성
        List<CartResponse> responses = cartItems.stream()
                .map(item -> {
                    Goods goods = goodsList.stream()
                            .filter(g -> g.getId().equals(item.goodsId))
                            .findFirst()
                            .orElse(null);

                    if (goods == null) {
                        log.warn("[Redis Cart] 상품을 찾을 수 없음: goodsId={}", item.goodsId);
                        return null;
                    }

                    String imageUrl = null;
                    if (goods.getImages() != null && !goods.getImages().isEmpty()) {
                        imageUrl = goods.getImages().get(0).getFileUrl();
                    }

                    // CartResponse에 cartId 대신 field를 사용 (삭제 시 필요)
                    return CartResponse.builder()
                            .cartId(item.goodsId) // 임시: goodsId를 cartId로 사용 (field 전달 필요)
                            .goodsId(item.goodsId)
                            .goodsName(goods.getGoodsName())
                            .imageUrl(imageUrl)
                            .price(goods.getPrice())
                            .totalAmount(item.cartItem.getAmount())
                            .totalPrice(item.cartItem.getTotalPrice())
                            .optionNumber(item.optionNumber)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 페이징 처리 (애플리케이션 레벨)
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), responses.size());

        if (start > responses.size()) {
            return Page.empty(pageable);
        }

        List<CartResponse> pagedResponses = responses.subList(start, end);
        return new PageImpl<>(pagedResponses, pageable, responses.size());
    }

    /**
     * 3. 장바구니 수량/옵션 변경
     *
     * Redis 명령어:
     * HGET cart:user:123 goods:1:option:null
     * HSET cart:user:123 goods:1:option:null '{"amount":5,"totalPrice":75000,...}'
     * EXPIRE cart:user:123 2592000
     */
    public void updateCartItem(Long goodsId, CartEditRequest request) {
        Member member = getMember();
        String cartKey = getCartKey(member.getId());

        // 기존 Field 삭제 (옵션 변경 가능성)
        String oldField = getCartField(goodsId, request.getOptionNumber());

        // 상품 조회
        Goods goods = goodsRepository.findById(goodsId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_GOODS));

        // 새로운 가격 계산
        int unitPrice = calculateUnitPrice(goods, request.getOptionNumber());

        // 새로운 Field (옵션 변경 시 field도 변경)
        String newField = getCartField(goodsId, request.getOptionNumber());

        // CartItem 생성
        CartItem cartItem = new CartItem(
                request.getAmount(),
                unitPrice * request.getAmount()
        );

        try {
            String cartItemJson = objectMapper.writeValueAsString(cartItem);

            // 옵션이 변경되었으면 기존 field 삭제
            if (!oldField.equals(newField)) {
                redisTemplate.opsForHash().delete(cartKey, oldField);
            }

            // 새로운 데이터 저장
            redisTemplate.opsForHash().put(cartKey, newField, cartItemJson);

            // TTL 갱신
            redisTemplate.expire(cartKey, Duration.ofDays(CART_TTL_DAYS));

            log.info("[Redis Cart] 장바구니 수정 성공: userId={}, goodsId={}, oldOption={}, newOption={}, amount={}",
                    member.getId(), goodsId, request.getOptionNumber(), request.getOptionNumber(), request.getAmount());
        } catch (JsonProcessingException e) {
            log.error("[Redis Cart] JSON 직렬화 실패", e);
            throw new RuntimeException("장바구니 수정 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 4. 장바구니 상품 삭제
     *
     * Redis 명령어:
     * HDEL cart:user:123 goods:1:option:null
     */
    public void removeFromCart(Long goodsId, Long optionNumber) {
        Member member = getMember();
        String cartKey = getCartKey(member.getId());
        String field = getCartField(goodsId, optionNumber);

        Long deleted = redisTemplate.opsForHash().delete(cartKey, field);

        if (deleted == 0) {
            throw new BusinessException(NOT_FOUND_CART);
        }

        log.info("[Redis Cart] 장바구니 삭제 성공: userId={}, goodsId={}, optionNumber={}",
                member.getId(), goodsId, optionNumber);
    }

    // ========== Helper Methods ==========

    /**
     * Redis Key 생성: cart:user:{userId}
     */
    private String getCartKey(Long userId) {
        return CART_KEY_PREFIX + userId;
    }

    /**
     * Redis Hash Field 생성: goods:{goodsId}:option:{optionNumber}
     */
    private String getCartField(Long goodsId, Long optionNumber) {
        String option = (optionNumber == null) ? "null" : optionNumber.toString();
        return "goods:" + goodsId + ":option:" + option;
    }

    /**
     * 단가 계산 (옵션 포함)
     */
    private int calculateUnitPrice(Goods goods, Long optionNumber) {
        if (optionNumber == null) {
            // 옵션 없는 상품
            return goods.getPrice();
        }

        // 옵션 있는 상품
        Options option = optionRepository.findByIdAndGoodsId(optionNumber, goods.getId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_OPTION));

        return option.getTotalPrice();
    }

    /**
     * 현재 로그인한 회원 조회
     */
    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return memberRepository.findByLoginId(authentication.getName())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
    }

    // ========== Inner Class ==========

    /**
     * Field와 CartItem을 함께 저장하는 내부 클래스
     */
    private static class CartItemWithKey {
        String field;
        Long goodsId;
        Long optionNumber;
        CartItem cartItem;

        public CartItemWithKey(String field, Long goodsId, Long optionNumber, CartItem cartItem) {
            this.field = field;
            this.goodsId = goodsId;
            this.optionNumber = optionNumber;
            this.cartItem = cartItem;
        }
    }
}
