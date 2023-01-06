package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Image;
import com.project.shop.goods.domain.Option;
import com.project.shop.goods.domain.OptionCreate;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionResponse implements Serializable {

    private List<OptionCreate> optionValue;
    private int totalPrice;
    private String optionDescription;

    public static List<OptionResponse> toResponse(Goods goods) {
        List<OptionResponse> list = new ArrayList<>();
        for (Option option : goods.getOptions()) {
            OptionResponse optionResponse = OptionResponse.builder()
                            .optionValue(option.getOptionValue())
                            .totalPrice(option.getTotalPrice())
                            .optionDescription(option.getOptionDescription())
                            .build();
            list.add(optionResponse);
        }

        return list;
    }
}
