package com.project.shop.goods.controller;

import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import com.project.shop.goods.controller.response.ReplyResponse;
import com.project.shop.goods.service.ReplyService;
import io.swagger.annotations.ApiOperation;
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
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @ApiOperation(value = "대댓글 등록")
    public void replyCreate(Long reviewId, @RequestBody @Valid ReplyCreateRequest request) {
        replyService.replyCreate(reviewId, request);
    }

    // 대댓글 수정
    @PutMapping("/replies/{replyId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ROLE_SELLER')")
    @ApiOperation(value = "대댓글 수정")
    public void replyEdit(@PathVariable("replyId") Long replyId, @RequestBody @Valid ReplyEditRequest request) {
        replyService.replyEdit(replyId, request);
    }

    // 대댓글 삭제
    @DeleteMapping("/replies/{replyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ROLE_SELLER','ROLE_ADMIN')")
    @ApiOperation(value = "대댓글 삭제")
    public void replyDelete(@PathVariable("replyId") Long replyId) {
        replyService.replyDelete(replyId);
    }
}
