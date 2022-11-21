package com.project.shop.goods.controller;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewResponse;
import com.project.shop.goods.service.Impl.ReviewServiceImpl;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    @GetMapping("/reviews")
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewResponse> reviewFindAll(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return reviewService.reviewFindAll(pageable);
    }

    // 리뷰 수정
    @PutMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public void reviewEdit(@PathVariable("reviewId") Long reviewId, Long memberId, @RequestBody @Valid ReviewEditRequest reviewEditRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        reviewService.reviewEdit(reviewId, member.getId(), reviewEditRequest);
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    @ResponseStatus(HttpStatus.OK)
    public void reviewDelete(@PathVariable("reviewId") Long reviewId, Long memberId) {
        reviewService.reviewDelete(reviewId, memberId);
    }


}
