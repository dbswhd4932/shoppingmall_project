package com.project.shop.goods.domain.enetity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_category")
@Entity
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_category_id")
    private Long id;                 //카테고리번호(PK)

    private String mainCategory;    //메인카테고리

    @OneToMany(mappedBy = "category")
    private List<Goods> goodsList = new ArrayList<>();

    public Category(String mainCategory) {
        this.mainCategory = mainCategory;
    }
}
