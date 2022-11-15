package com.project.shop.goods.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsEditRequest {

    private String goodsName;
    private String description;
    private int price;

    private List<MultipartFile> Images = new ArrayList<>();

}
