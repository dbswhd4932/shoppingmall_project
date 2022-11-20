package com.project.shop.goods.controller;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.Impl.ReviewServiceImpl;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ReviewControllerTest extends ControllerSetting {

    @MockBean
    ReviewServiceImpl reviewService;

    @MockBean
    MemberRepository memberRepository;

    @MockBean
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰생성")
    void viewCreateTest() throws Exception {
        //given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .orderItemId(1L)
                .memberId(1L)
                .comment("댓글")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when  then
        mockMvc.perform(post("/api/reviews")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

    }

    @Test
    @DisplayName("리뷰 전체조회")
    void reviewFindAllTest() throws Exception {
        //given
        Goods goods = GoodsFactory.createGoods();
        Review review = Review.builder().memberId(1L).goods(goods).comment("리뷰").build();

        String json = objectMapper.writeValueAsString(review);

        //when then
        mockMvc.perform(get("/api/reviews/all")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }

    @Test
    @Disabled // todo 재구현 후 테스트진행
    @DisplayName("리뷰 수정")
    void reviewEditTest() throws Exception {
        //given
    }

    @Test
    @Disabled // todo 재구현 후 테스트진행
    @DisplayName("리뷰 삭제")
    void reviewDeleteTest() {
        //given

    }
}