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
    private String goodsName;
    private String categoryName;
    private int price;
    private String description;
    private List<ImageResponse> imageList;
    private List<OptionResponse> options;

    public static GoodsResponse toResponse(Goods goods) {
        return GoodsResponse.builder()
                .memberId(goods.getMemberId())
                .goodsName(goods.getGoodsName())
                .categoryName(goods.getCategory().getCategory())
                .price(goods.getPrice())
                .description(goods.getGoodsDescription())
                .imageList(ImageResponse.toResponse(goods))
                .options(OptionResponse.toResponse(goods))
                .build();
    }
}
