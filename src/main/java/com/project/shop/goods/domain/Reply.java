package com.project.shop.goods.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.controller.request.ReplyEditRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "repliy")
@Entity
public class Reply extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repliy_id")
    private Long id;         //상품리뷰댓글번호(PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;   //상품리뷰(다대일)

    @Column(nullable = false)
    private String comment;  //리뷰댓글내용

    @Builder
    public Reply(Review review, String comment) {
        this.review = review;
        this.comment = comment;
    }

    public void edit(String comment) {
        this.comment = comment;
    }

}
