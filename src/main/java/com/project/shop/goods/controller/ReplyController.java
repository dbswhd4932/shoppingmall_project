package com.project.shop.goods.controller;

import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;
import com.project.shop.goods.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReplyController {

    private final ReplyService replyService;

    // 대댓글 생성
    @PostMapping("/replies")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SELLER')")
    public void replyCreate(@RequestBody @Valid ReplyCreateRequest request) {
        replyService.replyCreate(request);
    }

    // 대댓글 전체 조회
    @GetMapping("/replies")
    @ResponseStatus(HttpStatus.OK)
    public List<ReplyResponse> replyFind(Long reviewId) {
        return replyService.replyFind(reviewId);
    }

    // 대댓글 수정
    @PutMapping("/replies/{replyId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('SELLER')")
    public void replyEdit(@PathVariable("replyId") Long replyId, Long goodsMemberId, @RequestBody @Valid ReplyEditRequest request) {
        replyService.replyEdit(replyId, goodsMemberId, request);
    }

    // 대댓글 삭제
    @DeleteMapping("/replies/{replyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SELLER','ADMIN')")
    public void replyDelete(@PathVariable("replyId") Long replyId, Long goodsMemberId) {
        replyService.replyDelete(replyId, goodsMemberId);
    }
}
