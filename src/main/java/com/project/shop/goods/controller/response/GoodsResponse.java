package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Goods;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsResponse {

    private Long memberId;
    private Long goodsId;
    private String goodsName;
    private String categoryName;
    private int price;
    private String description;
    private List<ImageResponse> imageList;
    private List<OptionResponse> options;

    public GoodsResponse(Goods goods) {
        this.memberId = goods.getMemberId();
        this.goodsId = goods.getId();
        this.goodsName = goods.getGoodsName();
        this.categoryName = goods.getCategory().getCategory();
        this.price = goods.getPrice();
        this.imageList = ImageResponse.toResponse(goods);
        this.options = OptionResponse.toResponse(goods);

    }
}
