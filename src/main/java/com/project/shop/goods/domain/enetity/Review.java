package com.project.shop.goods.domain.enetity;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.domain.request.ReviewEditRequest;
import com.project.shop.member.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_review")
@Entity
public class Review extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_review_id")
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

    // 리뷰 수정
    public void edit(ReviewEditRequest reviewEditRequest) {
        this.comment = reviewEditRequest.getComment();
    }
}
