package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.OptionCreate;
import com.project.shop.goods.domain.Options;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionResponse {

    private List<OptionCreate> optionValue;
    private int totalPrice;
    private String optionDescription;

    public static List<OptionResponse> toResponse(Goods goods) {
        List<OptionResponse> list = new ArrayList<>();
        for (Options options : goods.getOptions()) {
            OptionResponse optionResponse = OptionResponse.builder()
                            .optionValue(options.getOptionValue())
                            .totalPrice(options.getTotalPrice())
                            .optionDescription(options.getOptionDescription())
                            .build();
            list.add(optionResponse);
        }

        return list;
    }
}
