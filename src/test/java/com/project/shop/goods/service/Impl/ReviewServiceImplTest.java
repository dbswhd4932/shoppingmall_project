package com.project.shop.goods.service.Impl;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.factory.ReviewFactory;
import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.goods.domain.enetity.Review;
import com.project.shop.goods.domain.request.ReviewCreateRequest;
import com.project.shop.goods.domain.request.ReviewEditRequest;
import com.project.shop.goods.domain.response.ReviewResponse;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.domain.entity.Order;
import com.project.shop.order.domain.entity.Pay;
import com.project.shop.order.repository.PayRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @InjectMocks
    ReviewServiceImpl reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    GoodsRepository goodsRepository;

    @Mock
    PayRepository payRepository;


    @Test // todo
    @Disabled
    @DisplayName("리뷰 생성")
    void reviewCreateTest() {
        //given
        Member member = MemberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(goodsRepository.findById(goods.getId())).willReturn(Optional.of(goods));
        ReviewCreateRequest reviewCreateRequest = ReviewFactory.reviewCreateRequest(member,goods);
        //when
        reviewService.reviewCreate(reviewCreateRequest);
        //then
        verify(memberRepository).findById(member.getId());
        verify(goodsRepository).findById(goods.getId());
        verify(reviewRepository).save(any());
    }

    @Test
    @DisplayName("리뷰 전체조회")
    void reviewFindAll() {
        //given
        Member member = MemberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Review review1 = ReviewFactory.createReview(member, goods);
        Review review2 = ReviewFactory.createReview(member, goods);
        given(reviewRepository.findAll()).willReturn(List.of(review1, review2));
        //when
        List<ReviewResponse> reviewResponses = reviewService.reviewFindAll();
        //then
        assertThat(reviewResponses.size()).isEqualTo(2);
    }

    @Test
    @Disabled // todo ??
    @DisplayName("리뷰 회원별 조회")
    void reviewFindMember() {
        //given
        Member member = MemberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Review review = ReviewFactory.createReview(member, goods);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));

        //when
        List<ReviewResponse> reviewResponses = reviewService.reviewFindMember(member.getId());
        //then
        assertThat(reviewResponses.size()).isEqualTo(1);

    }

    @Test
    @Disabled // todo ??
    @DisplayName("리뷰 수정")
    void reviewEditTest(){
        //given
        Long reviewId = 1L;
        Long memberId = 1L;
        Member member = MemberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        ReviewEditRequest editRequest = ReviewFactory.reviewEditRequest("테스트 리뷰 변경");
        Review review = ReviewFactory.createReview(member, goods);

        given(memberRepository.findById(reviewId)).willReturn(Optional.of(member));
        given(reviewRepository.findById(memberId)).willReturn(Optional.of(review));

        //when
        reviewService.reviewEdit(reviewId, memberId, editRequest);

        //then
        verify(reviewRepository).findById(review.getId());
    }

    @Test
    @Disabled
    @DisplayName("리뷰 삭제")
    void reviewDeleteTest(){
        //given
        Member member = MemberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Review review = ReviewFactory.createReview(member, goods);
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));
        //when
        reviewService.reviewDelete(review.getId(),member.getId());
        //then
        verify(reviewRepository).delete(review);
    }

}