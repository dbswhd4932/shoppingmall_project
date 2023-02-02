package com.project.shop.goods.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.shop.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)   //사진경로
    private String fileUrl;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @Builder
    public Image(String fileUrl, Goods goods) {
        this.fileUrl = fileUrl;
        this.goods = goods;
    }

    // 연관관계 편의 메서드 (양방향 매핑)
    public void setGoods(Goods goods) {
        this.goods = goods;
        goods.getImages().add(this);
    }
}

