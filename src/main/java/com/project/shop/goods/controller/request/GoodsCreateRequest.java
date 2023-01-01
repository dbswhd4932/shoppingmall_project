package com.project.shop.goods.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsCreateRequest {

    @NotNull(message = "상품이름을 입력하세요")
    @Size(min = 2, max = 50, message = "2~50 글자수 사이로 입력해주세요.")
    private String goodsName;

    @NotNull(message = "카테고리번호를 입력하세요.")
    private Long categoryId;

    @Min(value = 1000 , message = "가격은 1000원 이상이어야 합니다.")
    private int price;

    private List<OptionCreateRequest> optionCreateRequest;

    private String goodsDescription;

}
