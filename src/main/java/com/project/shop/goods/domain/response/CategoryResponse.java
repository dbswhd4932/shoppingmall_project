package com.project.shop.goods.domain.response;

import com.project.shop.goods.domain.enetity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {

    private String category;

    public static CategoryResponse toCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .category(category.getCategory()).build();
    }

}
