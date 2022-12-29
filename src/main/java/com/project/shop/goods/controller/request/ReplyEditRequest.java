package com.project.shop.goods.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyEditRequest {

    @NotBlank(message = "댓글을 입력하세요.")
    @Size(max = 255, message = "최대 255 글자까지만 작성할 수 있습니다.")
    private String comment;
}
