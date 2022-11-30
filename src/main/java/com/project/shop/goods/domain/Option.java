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
import java.util.*;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Option extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goods_id")
    private Goods goods;

    private String optionName; // 옵션이름

    // 옵션값
    @Convert(converter = OptionConverter.class)
    private Map<String, Object> optionValue;

    private int totalPrice;

    private String optionDescription;

    @Builder
    public Option(Goods goods, String optionName, Map<String, Object> optionValue, int totalPrice, String description) {
        this.goods = goods;
        this.optionName = optionName;
        this.optionValue = optionValue;
        this.totalPrice = totalPrice;
        this.optionDescription = description;
    }

    public static Option toOption(OptionCreateRequest optionCreateRequest, Goods goods) {
        return Option.builder()
                .goods(goods)
                .optionName(optionCreateRequest.getOptionName())
                .optionValue(optionCreateRequest.getOptionValue())
                .totalPrice(optionCreateRequest.getTotalPrice())
                .description(optionCreateRequest.getOptionDescription())
                .build();
    }



}
