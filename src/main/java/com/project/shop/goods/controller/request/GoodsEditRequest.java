package com.project.shop.goods.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsEditRequest {

    @NotNull(message = "상품이름은 필수 값입니다.")
    private String goodsName;

    private String description;

    @Min(value = 1000 , message = "가격은 1000원 이상이어야 합니다.")
    private int price;

}
