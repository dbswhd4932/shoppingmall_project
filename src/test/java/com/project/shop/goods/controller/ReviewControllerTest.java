package com.project.shop.goods.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.factory.GoodsFactory;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewResponse;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.Impl.ReviewServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ImportAutoConfiguration(WebSecurityConfig.class)
@DisplayName("리뷰 컨트롤러 테스트")
class ReviewControllerTest extends ControllerSetting {

    @MockBean
    ReviewServiceImpl reviewService;

    @MockBean
    ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰 생성")
    @WithMockUser(roles = "USER")
    void reviewCreate() throws Exception {
        //given
        ReviewCreateRequest reviewCreateRequest
                = ReviewCreateRequest.builder().orderItemId(1L).comment("comment").build();

        //when
        mockMvc.perform(post("/api/reviews")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequest)))
                .andExpect(status().isCreated());

        //then
        verify(reviewService).reviewCreate(refEq(reviewCreateRequest));
    }

    @Test
    @DisplayName("리뷰 조회")
    void reviewFindAll() throws Exception {
        //given
        ReviewResponse reviewResponse = ReviewResponse.builder().goodsId(1L).memberId(1L).comment("comment").build();
        Goods goods = GoodsFactory.createGoods();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        given(reviewService.reviewFindAll(goods.getId(), pageable)).willReturn(List.of(reviewResponse));

        //when then
        mockMvc.perform(get("/api/goods/reviews")
                        .queryParam("goodsId", "1")
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].comment").value("comment"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("리뷰 수정")
    @WithMockUser(roles = "USER")
    void reviewEdit() throws Exception {
        //given
        ReviewEditRequest reviewEditRequest = ReviewEditRequest.builder().comment("editComment").build();

        //when
        mockMvc.perform(put("/api/reviews/{reviewId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewEditRequest)))
                .andExpect(status().isOk());

        //then
        verify(reviewService).reviewEdit(anyLong(), refEq(reviewEditRequest));
    }

    @Test
    @DisplayName("리뷰 삭제")
    @WithMockUser(roles = "USER, ADMIN")
    void reviewDelete() throws Exception {
        //given
        //when
        mockMvc.perform(delete("/api/reviews/{reviewId}", 1L))
                .andExpect(status().isNoContent());

        //then
        verify(reviewService).reviewDelete(1L);

    }

}