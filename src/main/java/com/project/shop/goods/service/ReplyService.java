package com.project.shop.goods.service;

import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;
import com.project.shop.goods.domain.Review;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReplyService {

    // 리뷰 대댓글 생성
    void replyCreate(ReplyCreateRequest reviewReplyCreateRequest);

    // 리뷰 대댓글 조회
    List<ReplyResponse> replyFind(Long reviewId);

    // 리뷰 대댓글 수정
    void replyUpdate(Long replyId, Long goodsMemberId, ReplyEditRequest reviewReplyEditRequest);

    // 리뷰 대댓글 삭제
    void replyDelete(Long replyId, Long goodsMemberId);

}
