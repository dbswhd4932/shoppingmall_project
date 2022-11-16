package com.project.shop.goods.domain.enetity;

import com.project.shop.goods.domain.request.CategoryCreateRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
@Entity
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;                 //카테고리번호(PK)

    private String category;    //메인카테고리

    @Builder
    public Category(String category) {
        this.category = category;
    }

    public static Category toCategory(CategoryCreateRequest categoryCreateRequest) {
        return Category.builder()
                .category(categoryCreateRequest.getCategory())
                .build();
    }
}
