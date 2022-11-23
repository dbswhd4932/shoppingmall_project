package com.project.shop.goods.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.persistence.Column;
import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OptionCreateRequest {

    private int addPrice;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<LinkedMultiValueMap<String, String>> options = new ArrayList<>();

}
