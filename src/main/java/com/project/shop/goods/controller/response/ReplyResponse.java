package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Reply;
import com.project.shop.goods.domain.Review;
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

    public static ReplyResponse toReplyResponse(Reply reply) {
        return ReplyResponse.builder()
                .comment(reply.getComment())
                .build();
    }
}
