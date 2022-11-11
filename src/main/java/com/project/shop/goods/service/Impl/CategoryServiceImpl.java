package com.project.shop.goods.service.Impl;

import com.project.shop.goods.domain.enetity.Category;
import com.project.shop.goods.domain.request.CategoryCreateRequest;
import com.project.shop.goods.domain.response.CategoryResponse;
import com.project.shop.goods.repository.CategoryRepository;
import com.project.shop.goods.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 생성
    @Override
    public void categoryCreate(CategoryCreateRequest categoryCreateRequest) {
        Category category = Category.toCategory(categoryCreateRequest);
        categoryRepository.save(category);
    }

    // 카테고리 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> categoryFindAll() {
        return categoryRepository.findAll()
                .stream().map(CategoryResponse::toCategoryResponse)
                .collect(Collectors.toList());
    }

    // 카테고리 삭제
    @Override
    public void categoryDelete(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하는 카테고리가 아닙니다."));

        categoryRepository.delete(category);
    }
}
