package com.project.shop.goods.service.Impl;

import com.project.shop.goods.domain.Category;
import com.project.shop.goods.controller.request.CategoryCreateRequest;
import com.project.shop.goods.controller.response.CategoryResponse;
import com.project.shop.goods.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryService {

    @InjectMocks
    CategoryServiceImpl categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 생성")
    void categoryCreateTest() {
        //given
        CategoryCreateRequest request = CategoryCreateRequest.builder().category("의류").build();

        //when
        categoryService.categoryCreate(request);

        //then
        verify(categoryRepository).save(any());
    }

    @Test
    @DisplayName("카테고리 전체 조회")
    void categoryFindAll() {
        //given
        Category category1 = Category.builder().category("의류").build();
        Category category2 = Category.builder().category("신발").build();
        given(categoryRepository.findAll()).willReturn(List.of(category1, category2));

        //when
        List<CategoryResponse> categoryResponses = categoryService.categoryFindAll();

        //then
        assertThat(categoryResponses.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("카테고리 삭제")
    void categoryDelete() {
        //given
        Category category = Category.builder().category("의류").build();
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

        //when
        categoryService.categoryDelete(category.getId());

        //then
        verify(categoryRepository).delete(category);

    }

}