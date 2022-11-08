package com.project.shop.goods.domain.enetity;

import com.project.shop.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_review_repliy")
@Entity
public class ReviewRepliy extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_review_repliy_id")
    private Long id;        //상품리뷰댓글번호(PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_review_id")
    private Review review;          //상품리뷰(다대일)

    private String comment;                 //리뷰댓글내용



}
