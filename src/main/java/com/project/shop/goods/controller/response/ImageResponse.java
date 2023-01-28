package com.project.shop.goods.controller.response;

import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Image;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageResponse {

    private String fileUrl;

    public static List<ImageResponse> toResponse(Goods goods) {
        List<ImageResponse> list = new ArrayList<>();
        for (Image image : goods.getImages()) {
            ImageResponse imageResponse = ImageResponse.builder().fileUrl(image.getFileUrl()).build();
            list.add(imageResponse);
        }
        return list;
    }
}
