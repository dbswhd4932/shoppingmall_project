package com.project.shop.goods.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewResponse;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.ReviewService;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderItem;
import com.project.shop.order.domain.OrderStatus;
import com.project.shop.order.domain.Pay;
import com.project.shop.order.repository.OrderItemRepository;
import com.project.shop.order.repository.OrderRepository;
import com.project.shop.order.repository.PayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.shop.global.error.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;

    // 리뷰생성 - 결제한 사람만 리뷰 작성이 가능
    @Override
    public void reviewCreate(ReviewCreateRequest reviewCreateRequest) {
        // 주문_상품 DB 에서 요청회원의 주문이 있는지 확인
        OrderItem orderItem = orderItemRepository.findById(reviewCreateRequest.getOrderItemId())
                .orElseThrow(() -> new IllegalArgumentException("주문한 상품이 아닙니다."));

        // 주문_상품 의 회원과 리뷰 요청의 회원이 일치 && 주문상태가 결제완료 상태일때 리뷰 작성 가능
        if (orderItem.getMemberId().equals(reviewCreateRequest.getMemberId())
                && orderItem.getOrder().getStatus() == OrderStatus.COMPLETE) {
            Review review = Review.builder()
                    .memberId(orderItem.getMemberId())
                    .goods(orderItem.getGoods())
                    .comment(reviewCreateRequest.getComment())
                    .build();

            reviewRepository.save(review);
        } else {
            throw new IllegalArgumentException("주문한 상품이 아닙니다.");
        }
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

        if (!reviewList.isEmpty()) {
            return reviewList.stream().map(review -> ReviewResponse.toReviewResponse(review))
                    .collect(Collectors.toList());
        } else {
            throw new BusinessException(NOT_FOUND_REVIEW);
        }

    }

    // 리뷰 수정
    @Override
    public void reviewEdit(Long reviewId, Long memberId, ReviewEditRequest reviewEditRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_REVIEW));
        if (review.getMemberId().equals(memberId)) {
            review.edit(reviewEditRequest);
        } else {
            throw new BusinessException(ErrorCode.NOT_MATCH_REVIEW);
        }
    }

    // 리뷰 삭제
    @Override
    public void reviewDelete(Long reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_REVIEW));

        if (review.getMemberId().equals(memberId)) {
            reviewRepository.delete(review);
        } else {
            throw new BusinessException(CANT_DELETE_REVIEW);
        }
    }
}
