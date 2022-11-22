package com.project.shop.goods.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;
import com.project.shop.goods.domain.Reply;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.repository.ReplyRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.shop.global.error.ErrorCode.*;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class ReplyServiceImpl implements ReplyService {

    private final ReviewRepository reviewRepository;
    private final ReplyRepository replyRepository;

    // 대댓글 생성
    @Override
    public void replyCreate(ReplyCreateRequest replyCreateRequest) {
        Review review = reviewRepository.findById(replyCreateRequest.getReviewId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_REVIEW));

        review.checkSeller(replyCreateRequest);
        Reply reply = Reply.createReply(review, replyCreateRequest);
        replyRepository.save(reply);
    }

    // 대댓글 조회
    @Override
    @Transactional(readOnly = true)
    public List<ReplyResponse> replyFind(Long reviewId) {
        List<Reply> replyList = replyRepository.findByReviewId(reviewId);
        return replyList.stream()
                .map(reply -> ReplyResponse.toReplyResponse(reply)).collect(toList());
    }

    // 대댓글 수정
    @Override
    public void replyUpdate(Long replyId, Long goodsMemberId, ReplyEditRequest reviewReplyEditRequest) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_REPLY));

        reply.checkReply(goodsMemberId);
        reply.edit(reviewReplyEditRequest.getComment());
    }

    // 대댓글 삭제
    @Override
    public void replyDelete(Long replyId, Long goodsMemberId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new BusinessException(NOT_FOUND_REPLY));

        reply.checkReply(goodsMemberId);
        replyRepository.delete(reply);
    }
}
