package com.project.shop.goods.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionCreateRequest {

    private Map<String,Object> optionValue;

    private int totalPrice;

    private String optionDescription;

}
