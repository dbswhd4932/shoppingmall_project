package com.project.shop.goods.domain.enetity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.domain.request.GoodsCreateRequest;
import com.project.shop.goods.domain.request.GoodsEditRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

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
    @JoinColumn(name = "item_category_id")
    private Category category;  //카테고리(다대일)

    @Column(nullable = false)
    private int price;      //상품가격

    private String description; //상품설명

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL)
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
    // todo 이미지 수정
    public void update(GoodsEditRequest goodsEditRequest) {
        this.goodsName = goodsEditRequest.getGoodsName();
        this.description = goodsEditRequest.getDescription();
        this.price = goodsEditRequest.getPrice();
    }

}
