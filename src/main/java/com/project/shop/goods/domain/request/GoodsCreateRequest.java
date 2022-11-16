package com.project.shop.goods.domain.request;

import com.project.shop.goods.domain.enetity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoodsCreateRequest {

    private String goodsName;
    private Long memberId;
    private Category category;
    private int price;
    private String description;

}
