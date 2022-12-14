package com.project.shop.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartCreateRequest {

    @NotNull(message = "상품 번호를 입력해주세요.")
    private Long goodsId; // 상품 번호

    @NotNull(message = "구매 수량을 입력해주세요.")
    @Positive(message = "구매 수량은 1개 이상만 가능합니다.")
    private int amount; // 구매 수량

    @Nullable
    private Long optionNumber; // 옵션 번호

}
