package com.project.shop.goods.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.controller.request.CategoryCreateRequest;
import com.project.shop.goods.controller.request.CategoryEditRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
@Entity
public class Category extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;                 //카테고리번호(PK)

    @Column(unique = true)
    @JsonIgnore
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

    // 카테고리 수정
    public void editCategory(CategoryEditRequest categoryEditRequest) {
        this.category = categoryEditRequest.getCategory();
    }

}
