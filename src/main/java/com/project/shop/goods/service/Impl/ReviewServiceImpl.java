package com.project.shop.goods.service.Impl;

import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewPageResponse;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.ReviewService;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.domain.OrderItem;
import com.project.shop.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.project.shop.global.error.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final GoodsRepository goodsRepository;

    // 리뷰생성 - 결제한 사람만 리뷰 작성이 가능
    @Override
    public void reviewCreate(Long orderItemId, ReviewCreateRequest reviewCreateRequest) {
        Member member = getMember();

        OrderItem orderItem = orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new BusinessException(NO_BUY_ORDER));

        // 주문한 상품의 회원과 로그인한 회원이 다르면 예외처리
        if (!orderItem.getMemberId().equals(member.getId())) {
            throw new BusinessException(NOT_BUY_GOODS);
        }

        Goods goods = goodsRepository.findById(orderItem.getGoodsId()).orElseThrow(
                () -> new BusinessException(NOT_BUY_GOODS));

        Review review = Review.createReview(member, goods, reviewCreateRequest);
        reviewRepository.save(review);
    }

    // 리뷰 전체
    @Override
    @Transactional(readOnly = true)
    public List<ReviewPageResponse> reviewFindAll(Long goodsId, Pageable pageable) {
        Goods goods = goodsRepository.findById(goodsId).orElseThrow(() -> new BusinessException(NOT_FOUND_GOODS));
        Page<Review> reviews = reviewRepository.findAllByGoods(goods, pageable);
        List<ReviewPageResponse> list = new ArrayList<>();

        for (Review review : reviews) {
            ReviewPageResponse reviewPageResponse = ReviewPageResponse.toResponse(review, reviews);
            list.add(reviewPageResponse);
        }

        return list;
    }

    // 리뷰 수정
    @Override
    public void reviewEdit(Long reviewId, ReviewEditRequest reviewEditRequest) {
        Member member = getMember();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_REVIEW));

        // 리뷰를 작성한 사람과 로그인한 사람이 다르면 예외처리
        if (!review.getMemberId().equals(member.getId())) throw new BusinessException(NOT_MATCH_REVIEW);
        review.edit(reviewEditRequest);
    }

    // 리뷰 삭제
    @Override
    public void reviewDelete(Long reviewId) {
        Member member = getMember();

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_REVIEW));

        // 리뷰를 작성한 사람과 로그인한 사람이 다르면 예외처리
        if (!review.getMemberId().equals(member.getId())) throw new BusinessException(NOT_MATCH_REVIEW);
        reviewRepository.delete(review);
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));
        return member;
    }
}
