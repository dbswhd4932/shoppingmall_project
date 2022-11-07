package com.project.shop.goods.domain.enetity.dto;

import com.project.shop.goods.domain.enetity.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGoodsRequestDto {

    private Long id;
    private String name;
    private ItemCategory itemCategory;
    private int price;
    private String description;
    private List<MultipartFile> images = new ArrayList<>();
}
