package com.project.shop.goods.controller;

import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewResponse;
import com.project.shop.goods.service.ReviewService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 생성
    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER')")
    @ApiOperation(value = "리뷰 생성")
    public void reviewCreate(Long orderItemId, @RequestBody @Valid ReviewCreateRequest reviewCreateRequest) {
        reviewService.reviewCreate(orderItemId, reviewCreateRequest);
    }

    // 리뷰 전체조회
    @GetMapping("/goods/reviews")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "리뷰 조회")
    public List<ReviewResponse> reviewFindAll(Long goodsId, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return reviewService.reviewFindAll(goodsId, pageable);
    }

    // 리뷰 수정
    @PutMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER')")
    @ApiOperation(value = "리뷰 수정")
    public void reviewEdit(@PathVariable("reviewId") Long reviewId, @RequestBody @Valid ReviewEditRequest reviewEditRequest) {
        reviewService.reviewEdit(reviewId, reviewEditRequest);
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ApiOperation(value = "리뷰 삭제")
    public void reviewDelete(@PathVariable("reviewId") Long reviewId) {
        reviewService.reviewDelete(reviewId);
    }
}
