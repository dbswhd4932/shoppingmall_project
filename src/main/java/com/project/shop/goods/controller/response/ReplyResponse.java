package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Reply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyResponse {

    private String comment;

    public static ReplyResponse toResponse(Reply reply) {
        return ReplyResponse.builder()
                .comment(reply.getComment())
                .build();
    }
}
