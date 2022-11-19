package com.project.shop.goods.service.Impl;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.factory.ReviewFactory;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewResponse;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderItem;
import com.project.shop.order.domain.OrderStatus;
import com.project.shop.order.repository.OrderItemRepository;
import com.project.shop.order.repository.PayRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @InjectMocks
    ReviewServiceImpl reviewService;

    @Mock
    ReviewRepository reviewRepository;

    @Mock
    OrderItemRepository orderItemRepository;


    @Test
    @DisplayName("리뷰 생성")
    void reviewCreateTest() {
        //given
        Goods goods = GoodsFactory.createGoods();
        Order order = Order.builder().memberId(1L).name("수취인").phone("010").zipcode("우편번호")
                .detailAddress("상세주소").requirement("요청사항").totalPrice(1000).status(OrderStatus.COMPLETE).build();
        OrderItem orderItem = OrderItem.builder().memberId(1L).goods(goods).order(order).amount(10).orderPrice(1000).build();
        ReviewCreateRequest request = ReviewCreateRequest.builder().orderItemId(orderItem.getId()).memberId(1L).comment("댓글").build();
        given(orderItemRepository.findById(orderItem.getId())).willReturn(Optional.of(orderItem));

        //when
        reviewService.reviewCreate(request);

        //then
        verify(reviewRepository).save(any());
    }

    @Test
    @DisplayName("리뷰 전체조회")
    void reviewFindAll() {
        //given
        Goods goods = GoodsFactory.createGoods();
        Review review1 = ReviewFactory.createReview(goods);
        Review review2 = ReviewFactory.createReview(goods);
        given(reviewRepository.findAll()).willReturn(List.of(review1, review2));

        //when
        List<ReviewResponse> reviewResponses = reviewService.reviewFindAll();

        //then
        assertThat(reviewResponses.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("리뷰 수정")
    void reviewEditTest(){
        //given
        Goods goods = GoodsFactory.createGoods();
        Review review = ReviewFactory.createReview(goods);
        ReviewEditRequest editRequest = ReviewFactory.reviewEditRequest("테스트 리뷰 변경");
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));


        //when
        reviewService.reviewEdit(review.getId(), 1L, editRequest);

        //then
        verify(reviewRepository).findById(review.getId());
    }

    @Test
    @DisplayName("리뷰 삭제")
    void reviewDeleteTest(){
        //given
        Goods goods = GoodsFactory.createGoods();
        Review review = ReviewFactory.createReview(goods);
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));

        //when
        reviewService.reviewDelete(review.getId(), 1L);

        //then
        verify(reviewRepository).delete(review);

    }

}