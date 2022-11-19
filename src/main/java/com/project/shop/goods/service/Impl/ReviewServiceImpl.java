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
        OrderItem orderItem = orderItemRepository.findById(reviewCreateRequest.getOrderItemId())
                .orElseThrow(() -> new BusinessException(NO_BUY_ORDER));

        Review review = Review.builder()
                .memberId(reviewCreateRequest.getMemberId())
                .goods(orderItem.getGoods())
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

    // 리뷰 수정
    @Override
    public void reviewEdit(Long reviewId, Long memberId, ReviewEditRequest reviewEditRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_REVIEW));
        // 해당 회원이 작성한 리뷰가 아니면 예외
        if (!review.getMemberId().equals(memberId)) throw new BusinessException(ErrorCode.NOT_MATCH_REVIEW);
        review.edit(reviewEditRequest);
    }

    // 리뷰 삭제
    @Override
    public void reviewDelete(Long reviewId, Long memberId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_REVIEW));
        // 해당 회원이 작성한 리뷰가 아니면 예외
        if (!review.getMemberId().equals(memberId))  throw new BusinessException(CANT_DELETE_REVIEW);
        reviewRepository.delete(review);
    }
}
