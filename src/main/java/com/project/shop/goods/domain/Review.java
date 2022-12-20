package com.project.shop.goods.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.ReplyCreateRequest;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.member.domain.Member;
import com.project.shop.order.domain.OrderItem;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.project.shop.global.error.ErrorCode.NOT_MATCH_REVIEW;
import static com.project.shop.global.error.ErrorCode.NOT_SELLING_GOODS;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
@Entity
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;  //상품리뷰번호(PK)

    private Long memberId; // 회원 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;        //상품(다대일)

    @Column(nullable = false)
    private String comment;     //내용

    @Builder
    public Review(Long memberId, Goods goods, String comment) {
        this.memberId = memberId;
        this.goods = goods;
        this.comment = comment;
    }

    // 리뷰 생성
    public static Review createReview(Member member, OrderItem orderItem, ReviewCreateRequest reviewCreateRequest) {
        return Review.builder()
                .memberId(member.getId())
                .goods(orderItem.getGoods())
                .comment(reviewCreateRequest.getComment())
                .build();
    }

    // 리뷰 수정
    public void edit(ReviewEditRequest reviewEditRequest) {
        this.comment = reviewEditRequest.getComment();
    }

    // 상품을 판매하는 회원인지 확인
    public void checkSeller(ReplyCreateRequest replyCreateRequest) {
        if (!this.memberId.equals(replyCreateRequest.getProductMemberId()))
            throw new BusinessException(NOT_SELLING_GOODS);
    }

    // 해당 회원이 작성한 리뷰인지 확인
    public void checkWhoWriteReview(Long memberId) {
        if (!this.getMemberId().equals(memberId))
            throw new BusinessException(NOT_MATCH_REVIEW);
    }
}
