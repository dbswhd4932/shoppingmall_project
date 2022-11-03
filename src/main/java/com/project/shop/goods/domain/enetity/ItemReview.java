package com.project.shop.goods.domain.enetity;

import com.project.shop.global.common.BaseEntityTime;
import com.project.shop.member.domain.entity.Member;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item_review")
@Entity
public class ItemReview extends BaseEntityTime {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_review_id")
    private Long id;  //상품리뷰번호(PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;      //회원(다대일)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;        //상품(다대일)

    @Column(nullable = false)
    private String comment;     //내용

    private String img1;        //상품이미지1
    private String img2;        //상품이미지2
    private String img3;        //상품이미지3


}
