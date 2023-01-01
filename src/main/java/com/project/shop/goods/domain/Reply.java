package com.project.shop.goods.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.project.shop.global.error.ErrorCode.NOT_WRITE_REPLY;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "repliy")
@Entity
public class Reply extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repliy_id")
    private Long id;         //상품리뷰댓글번호(PK)

    @Column(nullable = false)
    private Long memberId;   //회원번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;   //상품리뷰(다대일)

    @Column(nullable = false)
    private String comment;  //리뷰댓글내용

    @Builder
    public Reply(Long memberId, Review review, String comment) {
        this.memberId = memberId;
        this.review = review;
        this.comment = comment;
    }

    public void edit(String comment) {
        this.comment = comment;
    }

    // 대댓글 생성
    public static Reply createReply(Member member, Review review, ReplyCreateRequest replyCreateRequest) {
        return Reply.builder()
                .memberId(member.getId())
                .review(review)
                .comment(replyCreateRequest.getReplyComment())
                .build();
    }

}
