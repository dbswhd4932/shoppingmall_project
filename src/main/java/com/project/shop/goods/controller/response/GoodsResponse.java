package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Goods;
import com.querydsl.core.annotations.QueryProjection;
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
    private String goodsDescription;
    private String memberLoginId;
    private String imageUrl; // 대표 이미지 URL
    private CategoryResponse category;
    private List<ImageResponse> imageList;
    private List<OptionResponse> options;

    @QueryProjection
    public GoodsResponse(Goods goods, String memberLoginId) {
        this.memberId = goods.getMemberId();
        this.goodsId = goods.getId();
        this.goodsName = goods.getGoodsName();
        this.categoryName = goods.getCategory().getCategory();
        this.price = goods.getPrice();
        this.goodsDescription = goods.getGoodsDescription();
        this.memberLoginId = memberLoginId;
        this.imageList = ImageResponse.toResponse(goods);
        this.options = OptionResponse.toResponse(goods);

        // 대표 이미지 URL 설정 (첫 번째 이미지)
        if (!goods.getImages().isEmpty()) {
            this.imageUrl = goods.getImages().get(0).getFileUrl();
        }

        // Category 응답 객체 생성
        this.category = CategoryResponse.toResponse(goods.getCategory());
    }

    public GoodsResponse(Goods goods) {
        this(goods, null);
    }
}
