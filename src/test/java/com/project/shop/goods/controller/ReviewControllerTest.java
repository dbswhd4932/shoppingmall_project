package com.project.shop.goods.controller;

import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReviewResponse;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.Impl.ReviewServiceImpl;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    @DisplayName("리뷰 생성")
    void viewCreateTest() throws Exception {
        //given
        ReviewCreateRequest reviewCreateRequest = ReviewCreateRequest.builder()
                .orderItemId(1L)
                .memberId(2L)
                .comment("댓글")
                .build();

        //when then
        mockMvc.perform(post("/api/reviews")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewCreateRequest)))
                .andExpect(status().isCreated());

        verify(reviewService).reviewCreate(refEq(reviewCreateRequest));
    }

    @Test
    @DisplayName("리뷰 전체조회")
    void reviewFindAll() throws Exception {
        //given
        Pageable pageable = PageRequest.of(0,10, Sort.Direction.DESC,"id");
        ReviewResponse reviewResponse = ReviewResponse.builder()
                .memberId(1L)
                .goodsId(2L)
                .comment("댓글")
                .build();

        given(reviewService.reviewFindAll(pageable)).willReturn(List.of(reviewResponse));

        //when then
        mockMvc.perform(get("/api/reviews")
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].comment").value("댓글"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("리뷰 수정")
    void reviewEdit() throws Exception {
        //given
        Member member = MemberFactory.createMember();
        ReviewEditRequest reviewEditRequest = ReviewEditRequest.builder().comment("리뷰수정").build();

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));

        //when then
        mockMvc.perform(put("/api/reviews/{reviewId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewEditRequest)))
                .andExpect(status().isOk());

        verify(reviewService).reviewEdit(any(), any(), refEq(reviewEditRequest));
    }

    @Test
    @DisplayName("리뷰 삭제")
    void reviewDeleteTest() throws Exception {
        //given
        //when then
        mockMvc.perform(delete("/api/reviews/{reviewId}", 1L)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(reviewService).reviewDelete(any(), any());
    }
}