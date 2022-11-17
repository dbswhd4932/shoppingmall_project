package com.project.shop.goods.controller.request;

import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OptionCreateRequest {

    private int addPrice;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private List<Map<String, String>> options;

}
