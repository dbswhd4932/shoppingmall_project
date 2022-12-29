package com.project.shop.goods.service;

import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;

import java.util.List;

public interface ReplyService {

    // 리뷰 대댓글 생성
    void replyCreate(Long reviewId, ReplyCreateRequest replyCreateRequest);

    // 리뷰 대댓글 조회
    List<ReplyResponse> replyFind(Long reviewId);

    // 리뷰 대댓글 수정
    void replyEdit(Long replyId, ReplyEditRequest ReplyEditRequest);

    // 리뷰 대댓글 삭제
    void replyDelete(Long replyId);

}
