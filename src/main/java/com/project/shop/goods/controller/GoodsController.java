package com.project.shop.goods.controller;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GoodsController {

    private final GoodsService goodsService;

    // 상품 등록
    @PostMapping(value = "/goods")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SELLER')")
    @ApiOperation(value = "상품 등록")
    public void goodsCreate(@RequestPart @Valid GoodsCreateRequest goodsCreateRequest,
                            @RequestPart List<MultipartFile> multipartFiles) throws IOException {

        goodsService.goodsCreate(goodsCreateRequest, multipartFiles);
    }

    // 상품 전체 조회
    @GetMapping("/goods")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 전체 조회")
    public Page<GoodsResponse> goodsFindAll(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return goodsService.goodsFindAll(pageable);
    }

    // 상품 가격 변경 확인
    @GetMapping("/goods/checkUpdateGoods")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 가격 변경 확인",
            notes = "changeCheck True = 변경 / False = 변경 없음")
    public List<UpdateGoodsResponse> checkGoodsUpdate(@RequestBody List<UpdateCheckRequest> updateCheckRequest) {
        return goodsService.checkGoodsUpdate(updateCheckRequest);
    }

    // 상품 단품 상세 조회
    @GetMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 단품 상세 조회")
    public GoodsResponse goodsDetailFind(@PathVariable("goodsId") Long goodsId) {
        return goodsService.goodsDetailFind(goodsId);
    }

    // 상품 검색
    @GetMapping("/goods/keyword")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 검색")
    public Page<GoodsResponse> goodsFindKeyword(@RequestParam String keyword,
                                                @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return goodsService.goodsFindKeyword(keyword, pageable);
    }

    // 상품 수정
    @PostMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SELLER')")
    @ApiOperation(value = "상품 수정")
    public void goodsEdit(@PathVariable("goodsId") Long goodsId,
                          @RequestPart @Valid GoodsEditRequest goodsEditRequest,
                          @RequestPart List<MultipartFile> multipartFiles) {

        goodsService.goodsEdit(goodsId, goodsEditRequest, multipartFiles);
    }

    // 상품 삭제
    @DeleteMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SELLER')")
    @ApiOperation(value = "상품 삭제")
    public void goodsDelete(@PathVariable("goodsId") Long goodsId) {
        goodsService.goodsDelete(goodsId);
    }

    //상품 ~원 이상 ~원 이하 값 찾기
    @GetMapping("/goods/search")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "상품 가격으로 검색 (0원 이상 0원 이하)")
    public Page<GoodsResponse> search(@ModelAttribute GoodsSearchCondition condition, Pageable pageable) {

        return goodsService.searchBetweenPrice(condition, pageable);
    }
}
