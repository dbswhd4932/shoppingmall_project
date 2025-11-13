package com.project.shop.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Redis에 저장될 장바구니 아이템 DTO
 *
 * 학습 포인트:
 * 1. Serializable: Redis 직렬화를 위해 필수
 * 2. 최소 정보만 저장: goodsId는 field에 포함되므로 중복 저장 안 함
 * 3. totalPrice 저장: 매번 계산하지 않고 저장 시 계산
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 수량
     * - 최소값: 1
     * - 최대값: 제한 없음 (비즈니스 로직에서 검증)
     */
    private Integer amount;

    /**
     * 총 가격 (단가 * 수량)
     * - 옵션이 있으면: (기본가 + 옵션가) * 수량
     * - 옵션이 없으면: 기본가 * 수량
     */
    private Integer totalPrice;

    /**
     * 메타데이터: 저장 시각 (옵션)
     * - 장바구니 정렬 시 사용 가능
     * - 나중에 추가된 상품이 위로 오도록
     */
    private Long addedAt;

    public CartItem(Integer amount, Integer totalPrice) {
        this.amount = amount;
        this.totalPrice = totalPrice;
        this.addedAt = System.currentTimeMillis();
    }
}
