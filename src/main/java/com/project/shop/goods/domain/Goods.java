package com.project.shop.goods.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.member.domain.Member;
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

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String goodsName;    //상품이름

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id")
    private Category category;  //카테고리(다대일)

    @Column(nullable = false)
    private int price;      //상품가격

    @Column
    private String goodsDescription; //상품설명

    // 상품 삭제 시 이미지 DB 도 같이 삭제 , cascade 옵션
    // null 처리된 자식을 delete -> orphanRemoval 옵션
    // JPA 순환참조 관련 @JsonManagedReference
    @JsonManagedReference
    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL)
    private List<Image> images = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL)
    private List<Options> options = new ArrayList<>();

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();


    @Builder
    public Goods(Long id, Long memberId, String goodsName, Category category, int price, String description) {
        this.id = id;
        this.memberId = memberId;
        this.goodsName = goodsName;
        this.category = category;
        this.price = price;
        this.goodsDescription = description;
    }


    public static Goods create(GoodsCreateRequest goodsCreateRequest, Category category, Member member) {
        return Goods.builder()
                .goodsName(goodsCreateRequest.getGoodsName())
                .memberId(member.getId())
                .category(category)
                .price(goodsCreateRequest.getPrice())
                .description(goodsCreateRequest.getGoodsDescription())
                .build();
    }

    // 업데이트 ( 상품이름, 설명 , 가격 )
    public void update(GoodsEditRequest goodsEditRequest) {
        this.goodsName = goodsEditRequest.getGoodsName();
        this.goodsDescription = goodsEditRequest.getGoodsDescription();
        this.price = goodsEditRequest.getPrice();
    }

}
