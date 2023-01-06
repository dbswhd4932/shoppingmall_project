package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Goods;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsPageResponse {

    private Long memberId;
    private Long goodsId;
    private String goodsName;
    private String categoryName;
    private int price;
    private String description;
    private List<ImageResponse> imageList;
    private List<OptionResponse> options;
    private int totalPage;
    private int totalCount;
    private int pageNumber;
    private int currentPageSize;

    public static GoodsPageResponse toResponse(Goods goods, Page page) {
        return GoodsPageResponse.builder()
                .memberId(goods.getMemberId())
                .goodsId(goods.getId())
                .goodsName(goods.getGoodsName())
                .categoryName(goods.getCategory().getCategory())
                .price(goods.getPrice())
                .description(goods.getGoodsDescription())
                .imageList(ImageResponse.toResponse(goods))
                .options(OptionResponse.toResponse(goods))
                .totalPage(page.getTotalPages())
                .totalCount((int) page.getTotalElements())
                .pageNumber(page.getNumber())
                .currentPageSize(page.getSize())
                .build();
    }


}
