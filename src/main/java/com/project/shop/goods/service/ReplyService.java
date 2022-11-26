package com.project.shop.goods.service;

import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;

import java.util.List;

public interface ReplyService {

    // 리뷰 대댓글 생성
    void replyCreate(ReplyCreateRequest ReplyCreateRequest);

    // 리뷰 대댓글 조회
    List<ReplyResponse> replyFind(Long reviewId);

    // 리뷰 대댓글 수정
    void replyEdit(Long replyId, Long goodsMemberId, ReplyEditRequest ReplyEditRequest);

    // 리뷰 대댓글 삭제
    void replyDelete(Long replyId, Long goodsMemberId);

}
