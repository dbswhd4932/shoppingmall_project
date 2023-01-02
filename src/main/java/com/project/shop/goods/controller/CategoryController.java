package com.project.shop.goods.controller;

import com.project.shop.goods.controller.request.CategoryCreateRequest;
import com.project.shop.goods.controller.request.CategoryEditRequest;
import com.project.shop.goods.controller.response.CategoryResponse;
import com.project.shop.goods.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "카테고리 생성")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void categoryCreate(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest) {
        categoryService.categoryCreate(categoryCreateRequest);
    }

    // 카테고리 전체조회
    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "카테고리 조회")
    public List<CategoryResponse> categoryFindAll() {
        return categoryService.categoryFindAll();
    }

    // 카테고리 수정
    @PutMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "카테고리 수정")
    public void categoryEdit(@PathVariable("categoryId") Long categoryId, @RequestBody CategoryEditRequest categoryEditRequest) {
        categoryService.categoryEdit(categoryId, categoryEditRequest);
    }

    // 카테고리 삭제
    @DeleteMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ApiOperation(value = "카테고리 삭제")
    public void categoryDelete(@PathVariable("categoryId") Long categoryId) {
        categoryService.categoryDelete(categoryId);
    }
}
