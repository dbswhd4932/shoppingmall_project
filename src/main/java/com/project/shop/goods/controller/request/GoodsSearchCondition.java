package com.project.shop.goods.controller.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsSearchCondition {

    private String keyword;          // 검색 키워드 (상품명)
    private Long categoryId;         // 카테고리 ID
    private Integer minPrice;        // 최소 가격
    private Integer maxPrice;        // 최대 가격

    // 기존 호환성을 위한 필드
    private Integer priceMin;
    private Integer priceMax;
    private String category;
}
