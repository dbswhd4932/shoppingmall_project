package com.project.shop.goods.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.factory.OrderFactory;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewResponse;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.ReviewService;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderItem;
import com.project.shop.order.repository.OrderItemRepository;
import com.project.shop.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ImportAutoConfiguration(WebSecurityConfig.class)
@DisplayName("리뷰 컨트롤러 통합테스트")
class ReviewControllerTest extends ControllerSetting {

    @Autowired
    ReviewService reviewService;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        Order order = OrderFactory.order(member);
        Goods goods = GoodsFactory.createGoods();
        goodsRepository.save(goods);
        orderRepository.save(order);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 생성")
    void reviewCreate() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Goods goods = goodsRepository.findByGoodsName("테스트상품").get();
        Order order = orderRepository.findByMerchantId("1111").get();
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder().comment("comment").build();
        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .goodsId(goods.getId())
                .memberId(member.getId())
                .goodsName("name")
                .orderPrice(1000)
                .amount(10)
                .price(10000)
                .build();
        orderItemRepository.save(orderItem);

        //when
        mockMvc.perform(post("/api/reviews")
                        .queryParam("orderItemId", String.valueOf(orderItem.getId()))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequest))
                        .with(user("loginId").roles("USER")))
                .andExpect(status().isCreated());

        //then
        assertThat(reviewRepository.findAll().size()).isEqualTo(1);

    }

    @Test
    @DisplayName("리뷰 전체 조회")
    void reviewFindAll() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Goods goods = goodsRepository.findByGoodsName("테스트상품").get();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        Review review = Review.builder().goods(goods).memberId(member.getId()).comment("comment").build();
        reviewRepository.save(review);

        Page<ReviewResponse> responses = reviewService.reviewFindAll(goods.getId(), pageable);

        //when
        mockMvc.perform(get("/api/goods/reviews")
                        .queryParam("goodsId", String.valueOf(goods.getId())))
                .andExpect(status().isOk());

        //then
        assertThat(responses.getTotalElements()).isEqualTo(1);

    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 수정")
    void reviewEdit() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Goods goods = goodsRepository.findByGoodsName("테스트상품").get();
        Review review = Review.builder().goods(goods).memberId(member.getId()).comment("comment").build();
        reviewRepository.save(review);

        ReviewEditRequest reviewEditRequest = ReviewEditRequest.builder().comment("commentEdit").build();

        //when
        mockMvc.perform(put("/api/reviews/{reviewId}", review.getId())
                        .contentType(APPLICATION_JSON)
                        .with(user("loginId").roles("USER"))
                        .content(objectMapper.writeValueAsString(reviewEditRequest)))
                .andExpect(status().isOk());
        //then
        assertThat(review.getComment()).isEqualTo("commentEdit");
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("리뷰 삭제")
    void reviewDelete() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Goods goods = goodsRepository.findByGoodsName("테스트상품").get();
        Review review = Review.builder().goods(goods).memberId(member.getId()).comment("comment").build();
        reviewRepository.save(review);

        //when
        mockMvc.perform(delete("/api/reviews/{reviewId}", review.getId())
                .with(user("loginId").roles("USER")))
                .andExpect(status().isNoContent());

        //then
        assertThat(reviewRepository.findAll().size()).isEqualTo(0);

    }


}