package com.project.shop.goods.service;

import com.project.shop.goods.domain.request.CategoryCreateRequest;
import com.project.shop.goods.domain.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    // 카테고리 생성
    void categoryCreate(CategoryCreateRequest categoryCreateRequest);

    // 카테고리 전체조회
    List<CategoryResponse> categoryFindAll();

    // 카테고리 삭제
    void categoryDelete(Long categoryId);
}
