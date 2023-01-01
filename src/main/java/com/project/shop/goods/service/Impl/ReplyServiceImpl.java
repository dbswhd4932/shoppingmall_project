package com.project.shop.goods.service.Impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;
import com.project.shop.goods.domain.Reply;
import com.project.shop.goods.domain.Review;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ReplyRepository;
import com.project.shop.goods.repository.ReviewRepository;
import com.project.shop.goods.service.ReplyService;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final MemberRepository memberRepository;

    // 대댓글 생성
    @Override
    public void replyCreate(Long reviewId, ReplyCreateRequest replyCreateRequest) {
        Member member = getMember();

        // 대댓글 추가할 리뷰 찾기
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_REVIEW));

        // 해당 상품을 판매하고 있는 회원인지 확인
        if(!review.getGoods().getMemberId().equals(member.getId())){
            throw new BusinessException(NOT_SELLING_GOODS);
        }

        Reply reply = Reply.createReply(member, review, replyCreateRequest);
        replyRepository.save(reply);
    }

    // 대댓글 조회
    @Override
    @Transactional(readOnly = true)
    public List<ReplyResponse> replyFind(Long reviewId) {
        List<Reply> replyList = replyRepository.findByReviewId(reviewId);

        // 대댓글이 없으면 예외처리
        if(replyList.isEmpty()) throw new BusinessException(NOT_FOUND_REPLY);

        return replyList.stream()
                .map(reply -> ReplyResponse.toResponse(reply)).collect(toList());
    }

    // 대댓글 수정
    @Override
    public void replyEdit(Long replyId, ReplyEditRequest ReplyEditRequest) {
        Member member = getMember();

        Reply reply = replyRepository.findByIdAndMemberId(replyId, member.getId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_REPLY));

        reply.edit(ReplyEditRequest.getComment());
    }

    // 대댓글 삭제
    @Override
    public void replyDelete(Long replyId) {
        Member member = getMember();

        Reply reply = replyRepository.findByIdAndMemberId(replyId, member.getId())
                .orElseThrow(() -> new BusinessException(NOT_FOUND_REPLY));

        replyRepository.delete(reply);
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Member member = memberRepository.findByLoginId(authentication.getName()).orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
        return member;
    }
}
