package com.project.shop.goods.controller;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.service.GoodsService;
import com.project.shop.goods.service.Impl.GoodsServiceImpl;
import com.project.shop.goods.service.Impl.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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

    // 상품 생성 , 이미지 O
    @PostMapping(value = "/goods")
    @ResponseStatus(HttpStatus.CREATED)
    public void goodsCreate(@RequestPart @Valid GoodsCreateRequest goodsCreateRequest,
                            @RequestPart List<MultipartFile> multipartFiles) throws IOException {

        if (goodsRepository.findByGoodsName(goodsCreateRequest.getGoodsName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);
        }

        List<String> imgPaths = s3Service.upload(multipartFiles); // s3 저장
       goodsService.goodsCreate(goodsCreateRequest, imgPaths);
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
    public List<GoodsResponse> goodsFindKeyword(@PathVariable String keyword, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return goodsService.goodsFindKeyword(pageable, keyword);
    }

    // 상품 수정
    @PutMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    public void goodsEdit(@PathVariable("goodsId") Long goodsId, Long memberId,
                          @RequestPart @Valid GoodsEditRequest goodsEditRequest,
                          @RequestPart(required = false) List<MultipartFile> multipartFiles) {

        // todo controller 에서 repository 의존
        if (goodsRepository.findByGoodsName(goodsEditRequest.getGoodsName()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_GOODS);
        }

        List<String> imgPaths = s3Service.upload(multipartFiles);
        goodsService.goodsEdit(goodsId, memberId, goodsEditRequest, imgPaths);
    }

    // 상품 삭제
    @DeleteMapping("/goods/{goodsId}")
    @ResponseStatus(HttpStatus.OK)
    public void goodsDelete(@PathVariable("goodsId") Long goodsId, Long memberId) {
        goodsService.goodsDelete(goodsId, memberId);
    }
}
