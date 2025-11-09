package com.project.shop.goods.controller;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.CategoryCreateRequest;
import com.project.shop.goods.controller.request.CategoryEditRequest;
import com.project.shop.goods.controller.response.CategoryResponse;
import com.project.shop.goods.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 생성 페이지 접근 권한 체크
    @GetMapping("/categories/check-access")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "카테고리 생성 페이지 접근 권한 체크")
    public Map<String, Object> checkCategoryCreateAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();

        // 비로그인 사용자
        if (authentication == null || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 로그인했지만 ADMIN 권한이 없는 경우
        boolean hasAdmin = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));

        if (!hasAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // ADMIN 권한이 있는 경우
        response.put("hasAccess", true);
        response.put("message", "카테고리 생성 페이지에 접근할 수 있습니다.");
        return response;
    }

    // 카테고리 생성
    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "카테고리 생성")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "카테고리 수정")
    public void categoryEdit(@PathVariable("categoryId") Long categoryId, @RequestBody CategoryEditRequest categoryEditRequest) {
        categoryService.categoryEdit(categoryId, categoryEditRequest);
    }

    // 카테고리 삭제
    @DeleteMapping("/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ApiOperation(value = "카테고리 삭제")
    public void categoryDelete(@PathVariable("categoryId") Long categoryId) {
        categoryService.categoryDelete(categoryId);
    }
}
