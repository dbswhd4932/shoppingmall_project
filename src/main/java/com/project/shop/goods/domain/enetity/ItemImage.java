package com.project.shop.goods.domain.enetity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 *  파일을 직접 DB 에 저장하면 부담이 되므로, 대체로 폴더에 따로 저장해두고
 *  파일 이름만 저장해서 가져온다.
 */

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @Column(nullable = false)
    private String originalName;    //파일 원본명

    @Column(nullable = false)
    private String filepath;        //파일 저장 경로


}
