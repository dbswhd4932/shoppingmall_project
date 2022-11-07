package com.project.shop.goods.domain.enetity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_category")
@Entity
public class ItemCategory {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_category_id")
    private Long id;                 //카테고리번호(PK)

    private String mainCategory;    //메인카테고리

    @OneToMany(mappedBy = "itemCategory")
    private List<Goods> goodsList = new ArrayList<>();

}
