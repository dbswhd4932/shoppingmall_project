package com.project.shop.goods.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "goods")
@Getter
@Entity
public class Goods extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goods_id")
    private Long id;   //상품번호(PK)

    private Long memberId;

    @Column(nullable = false, length = 100, unique = true)
    private String goodsName;    //상품이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;  //카테고리(다대일)

    @Column(nullable = false)
    private int price;      //상품가격

    private String description; //상품설명

    // 상품 삭제 시 이미지 DB 도 같이 삭제 , cascade 옵션
    // null 처리된 자식을 delete -> orphanRemoval 옵션
    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();


    @Builder
    public Goods(Long memberId, String goodsName, Category category, int price, String description, List<Image> images) {
        this.memberId = memberId;
        this.goodsName = goodsName;
        this.category = category;
        this.price = price;
        this.description = description;
        this.images = images;
    }


    public static Goods toGoods(GoodsCreateRequest goodsCreateRequest) {
        return Goods.builder()
                .goodsName(goodsCreateRequest.getGoodsName())
                .memberId(goodsCreateRequest.getMemberId())
                .category(goodsCreateRequest.getCategory())
                .price(goodsCreateRequest.getPrice())
                .description(goodsCreateRequest.getDescription())
                .build();
    }

    // 업데이트 ( 상품이름, 설명 , 가격 )
    // todo 이미지 수정 , 옵션  수정
    public void update(GoodsEditRequest goodsEditRequest) {
        this.goodsName = goodsEditRequest.getGoodsName();
        this.description = goodsEditRequest.getDescription();
        this.price = goodsEditRequest.getPrice();
    }

    // 이미지저장
    public void setImages(String storedFileName){
        for (Image image : images) {
            image.setFileUrl(storedFileName);
        }
    }

}
