package com.project.shop.goods.domain.enetity;

import com.project.shop.global.common.BaseEntityTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "goods")
@Entity
public class Goods extends BaseEntityTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "goods_id")
    private Long id;   //상품번호(PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id")
    private ItemCategory itemCategory;  //카테고리(다대일)

    @Column(nullable = false, length = 20, unique = true)
    private String name;    //상품이름

    @Column(nullable = false)
    private int price;      //상품가격

    private String description; //상품설명

    private String img1;    //상품이미지1
    private String img2;    //상품이미지2
    private String img3;    //상품이미지3

}
