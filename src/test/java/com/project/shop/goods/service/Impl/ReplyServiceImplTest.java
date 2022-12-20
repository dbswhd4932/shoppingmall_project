//package com.project.shop.goods.service.Impl;
//
//import com.project.shop.factory.GoodsFactory;
//import com.project.shop.factory.ReviewFactory;
//import com.project.shop.goods.controller.request.ReplyCreateRequest;
//import com.project.shop.goods.controller.request.ReplyEditRequest;
//import com.project.shop.goods.controller.response.ReplyResponse;
//import com.project.shop.goods.domain.Goods;
//import com.project.shop.goods.domain.Reply;
//import com.project.shop.goods.domain.Review;
//import com.project.shop.goods.repository.ReplyRepository;
//import com.project.shop.goods.repository.ReviewRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//
//@ExtendWith(MockitoExtension.class)
//class ReplyServiceImplTest {
//
//    @InjectMocks
//    ReplyServiceImpl replyService;
//
//    @Mock
//    ReplyRepository replyRepository;
//
//    @Mock
//    ReviewRepository reviewRepository;
//
//    @Test
//    @DisplayName("대댓글 생성")
//    void replyCreateTest() {
//        //given
//        Goods goods = GoodsFactory.createGoods();
//        Review review = ReviewFactory.createReview(goods);
//        ReplyCreateRequest request = ReplyCreateRequest.builder()
//                .reviewId(review.getId())
//                .productMemberId(1L)
//                .replyComment("대댓글")
//                .build();
//
//        given(reviewRepository.findById(request.getReviewId())).willReturn(Optional.of(review));
//        //when
//        replyService.replyCreate(request);
//
//        //then
//        verify(replyRepository).save(any());
//    }
//
//    @Test
//    @DisplayName("대댓글 조회")
//    void replyFindTest() {
//        //given
//        Goods goods = GoodsFactory.createGoods();
//        Review review = ReviewFactory.createReview(goods);
//        Reply reply = Reply.builder().review(review).comment("대댓글").build();
//        given(replyRepository.findByReviewId(review.getId())).willReturn(List.of(reply));
//
//        //when
//        List<ReplyResponse> replyResponses = replyService.replyFind(review.getId());
//
//        //then
//        assertThat(replyResponses.size()).isEqualTo(1);
//    }
//
//    @Test
//    @DisplayName("대댓글 수정")
//    void replyUpdateTest() {
//        //given
//        Goods goods = GoodsFactory.createGoods();
//        Review review = ReviewFactory.createReview(goods);
//        Reply reply = Reply.builder().review(review).comment("대댓글").build();
//        ReplyEditRequest request = ReplyEditRequest.builder().comment("대댓글수정").build();
//        given(replyRepository.findById(reply.getId())).willReturn(Optional.of(reply));
//
//        //when
//        replyService.replyEdit(reply.getId(),goods.getMemberId(), request);
//
//        //then
//        assertThat(reply.getComment()).isEqualTo(request.getComment());
//    }
//
//    @Test
//    @DisplayName("대댓글 삭제")
//    void replyDeleteTest() {
//        //given
//        Goods goods = GoodsFactory.createGoods();
//        Review review = ReviewFactory.createReview(goods);
//        Reply reply = Reply.builder().review(review).comment("대댓글").build();
//        given(replyRepository.findById(reply.getId())).willReturn(Optional.of(reply));
//
//        //when
//        replyService.replyDelete(reply.getId(),goods.getMemberId());
//
//        //then
//        verify(replyRepository).delete(reply);
//    }
//
//}