package com.project.shop.goods.service.Impl;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewPageResponse;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderItem;
import com.project.shop.order.repository.OrderItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
@DisplayName("리뷰 서비스 테스트")
class ReviewServiceImplTest {

    @InjectMocks
    ReviewServiceImpl reviewService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    OrderItemRepository orderItemRepository;

    @Mock
    GoodsRepository goodsRepository;

    @Mock
    ReviewRepository reviewRepository;

    @BeforeEach()
    void beforeEach() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TestingAuthenticationToken mockAuthentication = new TestingAuthenticationToken("loginId", "1234");
        context.setAuthentication(mockAuthentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("리뷰 생성")
    void reviewCreate() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder().comment("comment").build();
        Order order = Order.builder().memberId(member.getId()).merchantId("11").impUid("11").name("name")
                .phone("010").detailAddress("address").zipcode("zipcode").totalPrice(1000)
                .requirement("requirement").build();
        OrderItem orderItem = OrderItem.createOrderItem(member, goods.getId(), 1000, 10, order, "name", 1000);

        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(orderItemRepository.findById(1L)).willReturn(Optional.ofNullable(orderItem));
        given(goodsRepository.findById(orderItem.getGoodsId())).willReturn(Optional.of(goods));

        //when
        reviewService.reviewCreate(1L, reviewCreateRequest);

        //then
        verify(reviewRepository).save(any());

    }

    @Test
    @DisplayName("리뷰 전체")
    void reviewFindAll() {
        //given
        Goods goods = GoodsFactory.createGoods();
        Review review = new Review(1L, goods, "comment");
        List<Review> list = new ArrayList<>();
        list.add(review);

        PageImpl<Review> reviews = new PageImpl<>(list);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        given(goodsRepository.findById(goods.getId())).willReturn(Optional.of(goods));
        given(reviewRepository.findAllByGoods(goods, pageable)).willReturn(reviews);

        //when
        List<ReviewPageResponse> reviewResponses = reviewService.reviewFindAll(goods.getId(), pageable);

        //then
        assertThat(reviewResponses.get(0).getComment()).isEqualTo("comment");

    }

    @Test
    @DisplayName("리뷰 수정")
//    @WithMockUser(username = "test", password = "1234", roles = {"USER", "ADMIN"})
    void reviewEdit() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Review review = new Review(1L, goods, "comment");
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.ofNullable(member));

        ReviewEditRequest editComment = new ReviewEditRequest("editComment");
        //when
        reviewService.reviewEdit(1L, editComment);

        //then
        verify(reviewRepository).findById(any());
    }

    @Test
    @DisplayName("리뷰 삭제")
    void reviewDelete() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Review review = new Review(1L, goods, "comment");
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.ofNullable(member));
        given(reviewRepository.findById(review.getId())).willReturn(Optional.of(review));

        //when
        reviewService.reviewDelete(any());
        //then
        verify(reviewRepository).delete(review);
    }
}