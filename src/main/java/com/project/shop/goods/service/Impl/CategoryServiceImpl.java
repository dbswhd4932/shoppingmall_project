package com.project.shop.goods.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.CategoryEditRequest;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.controller.request.CategoryCreateRequest;
import com.project.shop.goods.controller.response.CategoryResponse;
import com.project.shop.goods.repository.CategoryRepository;
import com.project.shop.goods.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.shop.global.error.ErrorCode.CATEGORY_NAME_DUPLICATED;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 생성
    @Override
    public void categoryCreate(CategoryCreateRequest categoryCreateRequest) {
        if (categoryRepository.findByCategory(categoryCreateRequest.getCategory()).isPresent()) {
            throw new BusinessException(CATEGORY_NAME_DUPLICATED);
        }
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

    // 카테고리 수정
    @Override
    public void categoryEdit(Long categoryId, CategoryEditRequest categoryEditRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));

        category.editCategory(categoryEditRequest);
    }

    // 카테고리 삭제
    @Override
    public void categoryDelete(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CATEGORY));

        categoryRepository.delete(category);
    }
}
