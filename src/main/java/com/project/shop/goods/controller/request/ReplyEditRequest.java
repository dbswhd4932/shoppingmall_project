package com.project.shop.goods.controller.request;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class ReplyEditRequest {

    @NotBlank(message = "댓글을 입력하세요.")
    private String comment;
}
