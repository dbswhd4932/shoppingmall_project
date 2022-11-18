package com.project.shop.goods.service.Impl;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ImageRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@ExtendWith(MockitoExtension.class)
class GoodsServiceImplTest {

    @InjectMocks
    GoodsServiceImpl goodsService;

    @Mock
    GoodsRepository goodsRepository;

    @Mock
    ImageRepository imageRepository;


    @Test
    @Disabled // todo
    @DisplayName("상품 등록")
    void goodsCreateTest() throws IOException {
        //given
        GoodsCreateRequest goodsCreateRequest = GoodsCreateRequest.builder()
                .goodsName("상품이름")
                .memberId(1L)
                .category(new Category("의류"))
                .price(10000)
                .description("설명")
                .build();

        List<MultipartFile> mockMultipartFiles = List.of(
                new MockMultipartFile("test1", "test1.png", IMAGE_PNG_VALUE, "test1".getBytes()),
                new MockMultipartFile("test2", "test2.png", IMAGE_PNG_VALUE, "test2".getBytes()));

        //when

        //then

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

    // 상품 검색 (키워드)\
    // todo ImageRepository.findByGoodsId(java.lang.Long)" because "this.imageRepository" is null
    // todo 이미지 저장??
    @Test
    @Disabled
    @DisplayName("상품 검색")
    void goodsFindKeywordTest() {
        //given
        Goods goods1 = GoodsFactory.createGoods();
        Goods goods2 = GoodsFactory.createGoods();
        String keyword = "테스트상품";

        given(goodsRepository.findGoodsByGoodsNameContaining(keyword)).willReturn(List.of(goods1, goods2));

        //when
        List<GoodsResponse> responseList = goodsService.goodsFindKeyword(keyword);

        //then
        assertThat(responseList.size()).isEqualTo(2);
    }

}