package com.project.shop.member.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartEditRequest {

    @NotNull(message = "구매 수량을 입력해주세요.")
    @Positive(message = "구매 수량은 1개 이상만 가능합니다.")
    private int amount; // 구매 수량

    private int optionNumber; // 옵션번호

}
