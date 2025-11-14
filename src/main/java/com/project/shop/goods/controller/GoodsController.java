package com.project.shop.goods.controller;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.goods.controller.request.GoodsSearchCondition;
import com.project.shop.goods.controller.request.UpdateCheckRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.controller.response.UpdateGoodsResponse;
import com.project.shop.goods.service.GoodsService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GoodsController {

    private final GoodsService goodsService;

    // 상품 등록 페이지 접근 권한 체크 (특정 경로를 먼저 매핑)
    @GetMapping("/goods/check-access")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 등록 페이지 접근 권한 체크")
    public Map<String, Object> checkGoodsCreateAccess() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> response = new HashMap<>();

        // 비로그인 사용자
        if (authentication == null || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 로그인했지만 SELLER 권한이 없는 경우
        boolean hasSeller = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_SELLER"));

        if (!hasSeller) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        // SELLER 권한이 있는 경우
        response.put("hasAccess", true);
        response.put("message", "상품 등록 페이지에 접근할 수 있습니다.");
        return response;
    }

    // 상품 가격 변경 확인 (특정 경로를 먼저 매핑)
    @GetMapping("/goods/checkUpdateGoods")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 가격 변경 확인",
            notes = "changeCheck True = 변경 / False = 변경 없음")
    public List<UpdateGoodsResponse> checkGoodsUpdate(@RequestBody List<UpdateCheckRequest> updateCheckRequest) {
        return goodsService.checkGoodsUpdate(updateCheckRequest);
    }

    // 상품 검색 (특정 경로를 먼저 매핑)
    @GetMapping("/goods/keyword")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 검색")
    public Page<GoodsResponse> goodsFindKeyword(@RequestParam String keyword,
                                                @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return goodsService.goodsFindKeyword(keyword, pageable);
    }

    // 상품 가격으로 검색 (특정 경로를 먼저 매핑)
    @GetMapping("/goods/search")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 통합 검색 (키워드, 카테고리, 가격 범위)")
    public Page<GoodsResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        GoodsSearchCondition condition = GoodsSearchCondition.builder()
                .keyword(keyword)
                .categoryId(categoryId)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();

        return goodsService.searchGoods(condition, pageable);
    }

    // 상품 등록
    @PostMapping(value = "/goods")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @ApiOperation(value = "상품 등록")
    public void goodsCreate(@RequestPart @Valid GoodsCreateRequest goodsCreateRequest,
                            @RequestPart(required = false) List<MultipartFile> multipartFiles) throws IOException {

        goodsService.goodsCreate(goodsCreateRequest, multipartFiles);
    }

    // 상품 전체 조회 (카테고리 필터링 옵션)
    @GetMapping("/goods")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 전체 조회")
    public Page<GoodsResponse> goodsFindAll(
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        if (categoryId != null) {
            return goodsService.goodsFindByCategory(categoryId, pageable);
        }
        return goodsService.goodsFindAll(pageable);
    }

    // 상품 단품 상세 조회 (가장 마지막에 매핑 - 경로 변수 사용)
    @GetMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 단품 상세 조회")
    public GoodsResponse goodsDetailFind(@PathVariable("goodsId") Long goodsId) {
        return goodsService.goodsDetailFind(goodsId);
    }

    // 상품 수정
    @PostMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @ApiOperation(value = "상품 수정")
    public void goodsEdit(@PathVariable("goodsId") Long goodsId,
                          @RequestPart @Valid GoodsEditRequest goodsEditRequest,
                          @RequestPart(required = false) List<MultipartFile> multipartFiles) {

        goodsService.goodsEdit(goodsId, goodsEditRequest, multipartFiles);
    }

    // 상품 삭제
    @DeleteMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @ApiOperation(value = "상품 삭제")
    public void goodsDelete(@PathVariable("goodsId") Long goodsId) {
        goodsService.goodsDelete(goodsId);
    }
}
