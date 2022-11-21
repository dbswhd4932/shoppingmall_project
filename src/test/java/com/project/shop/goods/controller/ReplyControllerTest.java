package com.project.shop.goods.controller;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.ReviewFactory;
import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Reply;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ReplyRepository;
import com.project.shop.goods.service.Impl.ReplyServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import springfox.documentation.spring.web.json.Json;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReplyController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ReplyControllerTest extends ControllerSetting {

    @MockBean
    ReplyServiceImpl replyService;

    @MockBean
    ReplyRepository replyRepository;

    @MockBean
    GoodsRepository goodsRepository;

    @Test
    @DisplayName("대댓글 생성")
    void replyCreateTest() throws Exception {
        //given
        ReplyCreateRequest replyCreateRequest = ReplyCreateRequest.builder()
                .reviewId(1L)
                .productMemberId(1L)
                .replyComment("테스트")
                .build();

        // when then
        mockMvc.perform(post("/api/reply")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyCreateRequest)))
                .andExpect(status().isCreated());

        verify(replyService).replyCreate(any());

    }


    @Test
    @DisplayName("대댓글 조회")
    void replyFindTest() throws Exception {
        //given
        Goods goods = GoodsFactory.createGoods();
        Review review = ReviewFactory.createReview(goods);
        ReplyResponse replyResponse = ReplyResponse.builder().comment("테스트").build();
        given(replyService.replyFind(review.getId())).willReturn(List.of(replyResponse));

        //when then
        mockMvc.perform(get("/api/reply")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].comment").value("테스트"));

    }

    @Test
    @DisplayName("대댓글 수정")
    void replyEditTest() throws Exception {
        //given
        Goods goods = GoodsFactory.createGoods();
        Review review = ReviewFactory.createReview(goods);
        Reply reply = Reply.builder()
                .review(review)
                .comment("테스트")
                .build();

        ReplyEditRequest replyEditRequest = ReplyEditRequest.builder()
                .comment("수정")
                .build();

        given(replyRepository.findById(reply.getId())).willReturn(Optional.of(reply));

        //when then
        mockMvc.perform(put("/api/reply/{replyId}", 1L)
                        .param("goodsMemberId", "2")
                        .content(objectMapper.writeValueAsString(replyEditRequest))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(replyService).replyUpdate(any(), any(), refEq(replyEditRequest));

    }

    @Test
    @DisplayName("대댓글 삭제")
    void replyDeleteTest() throws Exception {
        //given
        //when then
        mockMvc.perform(delete("/api/reply/{replyId}", 1L)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(replyService).replyDelete(any(), any());
    }

}