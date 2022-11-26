package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Image;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsResponse {

    private Long memberId;
    private String goodsName;
    private String categoryName;
    private int price;
    private String description;
    private List<Image> imageList;

    public static GoodsResponse toGoodsResponse(Goods goods) {
        return GoodsResponse.builder()
                .memberId(goods.getMemberId())
                .goodsName(goods.getGoodsName())
                .categoryName(goods.getCategory().getCategory())
                .price(goods.getPrice())
                .description(goods.getDescription())
                .imageList(goods.getImages())
                .build();
    }
}
