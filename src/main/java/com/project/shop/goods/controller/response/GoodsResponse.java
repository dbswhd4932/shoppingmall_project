package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Image;
import com.project.shop.goods.domain.Option;
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
    private List<Option> options;

    public static GoodsResponse toGoodsResponse(Goods goods) {
        return GoodsResponse.builder()
                .memberId(goods.getMemberId())
                .goodsName(goods.getGoodsName())
                .categoryName(goods.getCategory().getCategory())
                .price(goods.getPrice())
                .description(goods.getGoodsDescription())
                .imageList(goods.getImages())
                .options(goods.getOptions())
                .build();
    }
}
