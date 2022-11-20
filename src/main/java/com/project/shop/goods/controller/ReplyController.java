package com.project.shop.goods.controller;

import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;
import com.project.shop.goods.service.Impl.ReplyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReplyController {

    private final ReplyServiceImpl replyService;

    // 대댓글 생성
    @PostMapping("/reply")
    @ResponseStatus(HttpStatus.CREATED)
    public void replyCreate(@RequestBody @Valid ReplyCreateRequest request) {
        replyService.replyCreate(request);
    }

    // 대댓글 조회
    @GetMapping("/reply")
    @ResponseStatus(HttpStatus.OK)
    public List<ReplyResponse> replyFind(Long reviewId) {
        return replyService.replyFind(reviewId);
    }

    // 대댓글 수정
    @PutMapping("/reply/{replyId}")
    @ResponseStatus(HttpStatus.OK)
    public void replyEdit(@PathVariable("replyId") Long replyId, Long goodsMemberId, @RequestBody @Valid ReplyEditRequest request) {
        replyService.replyUpdate(replyId, goodsMemberId, request);
    }

    // 대댓글 삭제
    @DeleteMapping("/reply/{replyId}")
    @ResponseStatus(HttpStatus.OK)
    public void replyDelete(@PathVariable("replyId") Long replyId, Long goodsMemberId) {
        replyService.replyDelete(replyId,goodsMemberId);
    }
}
