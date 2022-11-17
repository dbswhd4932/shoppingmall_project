package com.project.shop.goods.domain;

import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.List;
import java.util.Map;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class Option {

    @Id
    private Long id;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<Map<String, String>> options;

    private int addPrice;   // 추가금액

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "goods_id")
    private Goods goods;

    @Builder
    public Option(List<Map<String, String>> options, int addPrice, Goods goods) {
        this.options = options;
        this.addPrice = addPrice;
        this.goods = goods;
    }

    public static Option toOption(OptionCreateRequest optionCreateRequest) {
        return Option.builder()
                .options(optionCreateRequest.getOptions())
                .addPrice(optionCreateRequest.getAddPrice())
                .build();
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

}
