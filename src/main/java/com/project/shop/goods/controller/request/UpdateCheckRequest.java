package com.project.shop.goods.controller.request;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCheckRequest {

    @NotNull(message = "상품번호를 입력하세요.")
    private Long goodsId;

    @NotNull(message = "가격을 입력하세요.")
    private int goodsPrice;

    @Nullable
    private Long optionId;
}
