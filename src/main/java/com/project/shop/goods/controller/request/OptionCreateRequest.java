package com.project.shop.goods.controller.request;

import com.project.shop.goods.domain.OptionCreate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionCreateRequest {

    private List<OptionCreate> optionValue;

    private int totalPrice;

    private String optionDescription;

}
