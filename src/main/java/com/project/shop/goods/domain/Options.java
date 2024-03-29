package com.project.shop.goods.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.shop.global.common.BaseTimeEntity;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.domain.convert.OptionConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Options extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;

    // 옵션값
    @Convert(converter = OptionConverter.class)
    private List<OptionCreate> optionValue;

    @Column(nullable = false)
    private int totalPrice;

    @Column
    private String optionDescription;

    @Builder
    public Options(Long id, Goods goods, List<OptionCreate> optionValue, int totalPrice, String description) {
        this.id = id;
        this.goods = goods;
        this.optionValue = optionValue;
        this.totalPrice = totalPrice;
        this.optionDescription = description;
    }

    // 연관관계 편의 메서드 (양방향 매핑)
    public void setGoods(Goods goods) {
        this.goods = goods;
        goods.getOptions().add(this);
    }

    public static Options toOption(OptionCreateRequest optionCreateRequest, Goods goods) {
        return Options.builder()
                .goods(goods)
                .optionValue(optionCreateRequest.getOptionValue())
                .totalPrice(optionCreateRequest.getTotalPrice())
                .description(optionCreateRequest.getOptionDescription())
                .build();
    }

}
