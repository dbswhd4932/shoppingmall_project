package com.project.shop.goods.controller.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateGoodsResponse {

    private Long goodsId;
    private int goodsPrice;
    private boolean changeCheck;

}
