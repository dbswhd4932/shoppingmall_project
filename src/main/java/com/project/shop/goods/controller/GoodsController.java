package com.project.shop.goods.controller;

import com.project.shop.goods.domain.request.GoodsCreateRequest;
import com.project.shop.goods.domain.request.GoodsEditRequest;
import com.project.shop.goods.domain.response.GoodsResponse;
import com.project.shop.goods.service.Impl.GoodsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GoodsController {

    private final GoodsServiceImpl goodsService;

    // 상품 생성
    @PostMapping(value = "/goods", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public void goodsCreate(@RequestPart GoodsCreateRequest goodsCreateRequest,
                            @RequestPart List<MultipartFile> files) throws IOException {
        goodsService.goodsCreate(goodsCreateRequest, files);
    }

    // 상품 전체 검색
    @GetMapping("/goods")
    @ResponseStatus(HttpStatus.OK)
    public List<GoodsResponse> goodsFindAll(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return goodsService.goodsFindAll(pageable);
    }


    // 상품 검색 (키워드)
    @GetMapping("/goods/{keyword}")
    @ResponseStatus(HttpStatus.OK)
    public List<GoodsResponse> goodsFindKeyword(@PathVariable String keyword) {
        return goodsService.goodsFindKeyword(keyword);
    }

    // 상품 수정
    // todo 이미지 수정 구현필요
    @PutMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    public void goodsEdit(@PathVariable("goodsId") Long goodsId, @RequestBody GoodsEditRequest goodsEditRequest) {
        goodsService.goodsEdit(goodsId, goodsEditRequest);
    }

    // 상품 삭제
    @DeleteMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    public void goodsDelete(@PathVariable("goodsId") Long goodsId) {
        goodsService.goodsDelete(goodsId);
    }
}