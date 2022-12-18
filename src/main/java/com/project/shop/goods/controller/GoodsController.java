package com.project.shop.goods.controller;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.service.GoodsService;
import com.project.shop.goods.service.Impl.S3Service;
import lombok.RequiredArgsConstructor;
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

    private final S3Service s3Service;
    private final GoodsService goodsService;
    private final GoodsRepository goodsRepository;

    // 상품 생성
    @PostMapping(value = "/goods")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SELLER')")
    public void goodsCreate(@RequestPart @Valid GoodsCreateRequest goodsCreateRequest,
                            @RequestPart List<MultipartFile> multipartFiles) throws IOException {

        if (goodsRepository.findByGoodsName(goodsCreateRequest.getGoodsName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);
        }

        List<String> imgPaths = s3Service.upload(multipartFiles); // s3 저장
       goodsService.goodsCreate(goodsCreateRequest, imgPaths);
    }

    // 상품 전체 조회
    @GetMapping("/goods")
    @ResponseStatus(HttpStatus.OK)
    public List<GoodsResponse> goodsFindAll(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return goodsService.goodsFindAll(pageable);
    }

    // 상품 단품 상세 조회
    @GetMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    public GoodsResponse goodsDetailFind(@PathVariable("goodsId") Long goodsId) {
        return goodsService.goodsDetailFind(goodsId);
    }

    // 상품 검색
    @GetMapping("/goods/keyword")
    @ResponseStatus(HttpStatus.OK)
    public List<GoodsResponse> goodsFindKeyword(@RequestParam String keyword, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return goodsService.goodsFindKeyword(keyword, pageable);
    }

    // 상품 수정
    @PutMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SELLER')")
    public void goodsEdit(@PathVariable("goodsId") Long goodsId,
                          @RequestPart @Valid GoodsEditRequest goodsEditRequest,
                          @RequestPart(required = false) List<MultipartFile> multipartFiles) {

        if (goodsRepository.findByGoodsName(goodsEditRequest.getGoodsName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);
        }

        List<String> imgPaths = s3Service.upload(multipartFiles);
        goodsService.goodsEdit(goodsId, goodsEditRequest, imgPaths);
    }

    // 상품 삭제
    @DeleteMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SELLER')")
    public void goodsDelete(@PathVariable("goodsId") Long goodsId) {
        goodsService.goodsDelete(goodsId);
    }
}
