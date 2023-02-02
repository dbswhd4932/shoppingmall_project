package com.project.shop.goods.domain;

import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.controller.request.ReviewCreateRequest;
import com.project.shop.goods.controller.request.ReviewEditRequest;
import com.project.shop.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "review")
@Entity
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;  //상품리뷰번호(PK)

    @Column(nullable = false)
    private Long memberId; // 회원 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;        //상품(다대일)

    @Column(nullable = false)
    private String comment;     //내용

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<Reply> replies = new ArrayList<>();

    @Builder
    public Review(Long memberId, Goods goods, String comment) {
        this.memberId = memberId;
        this.goods = goods;
        this.comment = comment;
    }

    // 연관관계 편의 메서드 (양방향 매핑)
    public void setGoods(Goods goods) {
        this.goods = goods;
        goods.getReviews().add(this);
    }

    // 리뷰 생성
    public static Review createReview(Member member, Goods goods, ReviewCreateRequest reviewCreateRequest) {
        return Review.builder()
                .memberId(member.getId())
                .goods(goods)
                .comment(reviewCreateRequest.getComment())
                .build();
    }

    // 리뷰 수정
    public void edit(ReviewEditRequest reviewEditRequest) {
        this.comment = reviewEditRequest.getComment();
    }
}
