package com.project.shop.goods.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;    //사진이름

    @Column(nullable = false)   //사진경로
    private String fileUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    @JsonIgnore
    private Goods goods;


    @Builder
    public Image(String fileName, String fileUrl, Goods goods) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.goods = goods;
    }

    public void setFileUrl(String fileName) {
        this.fileName = fileName;
    }
}

