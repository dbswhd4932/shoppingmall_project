package com.project.shop.goods.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.goods.domain.enetity.Review;
import com.project.shop.goods.domain.request.ReviewCreateRequest;
import com.project.shop.goods.domain.request.ReviewEditRequest;
import com.project.shop.goods.domain.response.ReviewResponse;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.ReviewService;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final GoodsRepository goodsRepository;
    private final MemberRepository memberRepository;

    // 리뷰생성
    @Override
    public void reviewCreate(ReviewCreateRequest reviewCreateRequest) {
        Goods goods = goodsRepository.findById(reviewCreateRequest.getGoodsId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));

        Member member = memberRepository.findById(reviewCreateRequest.getMemberId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));


        Review review = Review.builder()
                .memberId(member.getId())
                .goods(goods)
                .comment(reviewCreateRequest.getComment())
                .build();

        reviewRepository.save(review);

    }

    // 리뷰 전체조회
    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> reviewFindAll() {
        return reviewRepository.findAll()
                .stream().map(ReviewResponse::toReviewResponse).collect(Collectors.toList());
    }

    // 리뷰 회원별 조회
    @Override
    public List<ReviewResponse> reviewFindMember(Long memberId) {
        List<Review> reviewList = reviewRepository.findByMemberId(memberId);

        if ( !reviewList.isEmpty()) {
            return reviewList.stream().map(review -> ReviewResponse.toReviewResponse(review))
                    .collect(Collectors.toList());
        } else {
            throw new BusinessException(ErrorCode.NOT_FOUND_REVIEW);
        }

    }

    // 리뷰 수정
    @Override
    public void reviewEdit(Long reviewId, Long memberId, ReviewEditRequest reviewEditRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_REVIEW));
        if (review.getMemberId().equals(memberId)) {
            review.edit(reviewEditRequest);
        } else {
            throw new BusinessException(ErrorCode.NOT_MATCH_REVIEW);
        }
    }

    // 리뷰 삭제
    @Override
    public void reviewDelete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_REVIEW));
        reviewRepository.delete(review);
    }
}
