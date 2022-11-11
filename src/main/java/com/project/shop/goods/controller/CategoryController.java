package com.project.shop.goods.controller;

import com.project.shop.goods.domain.request.CategoryCreateRequest;
import com.project.shop.goods.domain.response.CategoryResponse;
import com.project.shop.goods.service.Impl.CategoryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryServiceImpl categoryService;

    // 카테고리 생성
    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public void categoryCreate(@RequestBody CategoryCreateRequest categoryCreateRequest) {
        categoryService.categoryCreate(categoryCreateRequest);
    }

    // 카테고리 전체조회
    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryResponse> categoryFindAll() {
        return categoryService.categoryFindAll();
    }

    // 카테고리 삭제
    @DeleteMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    public void categoryDelete(@PathVariable("categoryId") Long categoryId) {
        categoryService.categoryDelete(categoryId);
    }
}
