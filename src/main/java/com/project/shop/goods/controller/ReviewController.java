package com.project.shop.goods.controller;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewResponse;
import com.project.shop.goods.service.Impl.ReviewServiceImpl;
import com.project.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewServiceImpl reviewService;
    private final MemberRepository memberRepository;

    // 리뷰 생성
    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public void reviewCreate(@RequestBody @Valid ReviewCreateRequest reviewCreateRequest) {
        reviewService.reviewCreate(reviewCreateRequest);
    }

    // 리뷰 전체조회
    @GetMapping("/reviews/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> reviewFindAll() {
        return reviewService.reviewFindAll();
    }

    // 리뷰 회원 별 조회
    @GetMapping("/reviews")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> reviewFindMember(Long memberId) {

        return reviewService.reviewFindMember(memberId);
    }


    // 리뷰 수정
    @PutMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public void reviewEdit(@PathVariable("reviewId") Long reviewId, Long memberId, @RequestBody @Valid ReviewEditRequest reviewEditRequest) {
        memberRepository.findById(memberId).orElseThrow(()-> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        reviewService.reviewEdit(reviewId, memberId, reviewEditRequest);
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public void reviewDelete(@PathVariable("reviewId") Long reviewId, Long memberId) {
        reviewService.reviewDelete(reviewId, memberId );
    }


}
