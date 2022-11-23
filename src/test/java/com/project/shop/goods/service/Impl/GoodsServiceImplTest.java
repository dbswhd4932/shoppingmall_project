package com.project.shop.goods.service.Impl;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.domain.Image;
import com.project.shop.goods.domain.Option;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ImageRepository;
import com.project.shop.goods.repository.OptionRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@ExtendWith(MockitoExtension.class)
class GoodsServiceImplTest {

    @InjectMocks
    GoodsServiceImpl goodsService;

    @Mock
    GoodsRepository goodsRepository;


    @Test
    @Disabled
    @DisplayName("상품 생성")
    void goodsCreateTest() throws IOException {
        //given
        GoodsCreateRequest goodsCreateRequest = GoodsCreateRequest.builder()
                .goodsName("상품이름")
                .memberId(1L)
                .category(new Category("의류"))
                .price(10000)
                .build();

        Goods goods = Goods.toGoods(goodsCreateRequest);
        given(goodsRepository.save(goods)).willReturn(goods);
        given(goodsRepository.findById(goods.getId())).willReturn(Optional.of(goods));
        given(goodsRepository.findByGoodsName(goodsCreateRequest.getGoodsName())).willReturn(null);
        //when
//        goodsService.goodsCreate(refEq(goodsCreateRequest), Collections.emptyList(), null);

        //then
        verify(goodsRepository).save(goods);
    }

    @Test
    @DisplayName("상품 전체 조회")
    void goodsFindAllTest() {
        //given
        Pageable pageable = PageRequest.of(0,10, Sort.Direction.DESC,"id");
        List<Goods> goodsList = goodsRepository.findAll();
        Goods goods = GoodsFactory.createGoods();
        goodsList.add(goods);
        Page<Goods> goodsPage = new PageImpl<>(goodsList);
        given(goodsRepository.findAll(pageable)).willReturn(goodsPage);

        //when
        List<GoodsResponse> goodsResponses = goodsService.goodsFindAll(pageable);

        //then
        assertThat(goodsResponses.size()).isEqualTo(1);

    }

}