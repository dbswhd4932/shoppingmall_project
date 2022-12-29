package com.project.shop.goods.controller.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCheckRequest {

    private Long goodsId;
    private int goodsPrice;
    private Long optionId;
}
