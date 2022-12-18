package com.project.shop.goods.controller.request;

import com.project.shop.goods.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsCreateRequest {

    @NotNull(message = "상품이름을 입력하세요")
    private String goodsName;

    @NotNull(message = "카테고리를 입력하세요.")
    private Category category;

    @Min(value = 1000 , message = "가격은 1000원 이상이어야 합니다.")
    private int price;

    @Column
    private List<OptionCreateRequest> optionCreateRequest;

    @Column
    private String goodsDescription;

}
