package com.project.shop.goods.domain.enetity;

import com.project.shop.global.common.BaseEntityTime;
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
public class Goods extends BaseEntityTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "goods_id")
    private Long id;   //상품번호(PK)

    @Column(nullable = false, length = 20, unique = true)
    private String name;    //상품이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_category_id")
    private ItemCategory itemCategory;  //카테고리(다대일)

    @Column(nullable = false)
    private int price;      //상품가격

    private String description; //상품설명

    @OneToMany(mappedBy = "goods", cascade = CascadeType.REMOVE) // 상품이 삭제될때 같이 삭제
    private List<ItemImage> itemImages = new ArrayList<>();

    @Builder
    public Goods(Long id, String name, ItemCategory itemCategory, int price, String description, List<ItemImage> itemImages) {
        this.id = id;
        this.name = name;
        this.itemCategory = itemCategory;
        this.price = price;
        this.description = description;
        this.itemImages = itemImages;
    }

}
