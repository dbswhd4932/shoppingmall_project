package com.project.shop.goods.controller.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateRequest {

    @NotBlank(message = "카테고리는 필수 값 입니다.")
    private String category;

}
