package com.project.shop.goods.controller.request;

import com.project.shop.goods.domain.OptionCreate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionCreateRequest {

    @NotNull(message = "옵션 입력하세요.")
    private List<OptionCreate> optionValue;

    @NotNull(message = "총 가격을 입력하세요.")
    private int totalPrice;

    @Nullable
    private String optionDescription;

}
