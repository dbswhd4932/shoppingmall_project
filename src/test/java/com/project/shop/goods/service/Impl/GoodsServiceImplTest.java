package com.project.shop.goods.service.Impl;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ImageRepository;
import com.project.shop.goods.repository.OptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GoodsServiceImplTest {

    @InjectMocks
    GoodsServiceImpl goodsService;

    @Mock
    GoodsRepository goodsRepository;

    @Mock
    OptionRepository optionRepository;

    @Mock
    ImageRepository imageRepository;

    @Mock
    S3Service s3Service;

//    @Test
//    @DisplayName("상품 생성")
//    void goodsCreateTest() {
//        //given
//        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
//                .optionValue(null)
//                .totalPrice(12000)
//                .optionDescription("옵션설명").build();
//
//        GoodsCreateRequest goodsCreateRequest = GoodsCreateRequest.builder()
//                .goodsName("상품이름")
//                .category(new Category("의류"))
//                .price(10000)
//                .optionCreateRequest(List.of(optionCreateRequest))
//                .goodsDescription("상품설명")
//                .build();
//
//        List<String> impPaths = new ArrayList<>();
//        impPaths.add("abcdefg.png");
//        Goods goods = Goods.create(goodsCreateRequest);
//
//        //when
//        goodsService.goodsCreate(goodsCreateRequest, impPaths);
//
//        //then
//        verify(goodsRepository).save(refEq(goods));
//        verify(optionRepository).save(any()); // 어떤값이라도 상관없다.
//        verify(imageRepository).save(any());
//
//    }

//    @Test
//    @DisplayName("상품 전체 조회")
//    void goodsFindAllTest() {
//        //given
//        Pageable pageable = PageRequest.of(0,10, Sort.Direction.DESC,"id");
//        List<Goods> goodsList = goodsRepository.findAll();
//        Goods goods = GoodsFactory.createGoods();
//        goodsList.add(goods);
//        Page<Goods> goodsPage = new PageImpl<>(goodsList);
//        given(goodsRepository.findAll(pageable)).willReturn(goodsPage);
//
//        //when
//        List<GoodsResponse> goodsResponses = goodsService.goodsFindAll(pageable);
//
//        //then
//        assertThat(goodsResponses.size()).isEqualTo(1);
//
//    }

    @Test
    @DisplayName("상품 검색")
    void goodFindKeyWordTest() {
        //given
        Pageable pageable = PageRequest.of(0,10, Sort.Direction.DESC,"id");
        String keyword = "테스트";
        Goods goods = GoodsFactory.createGoods();
        given(goodsRepository.findGoodsByGoodsNameContaining(pageable,keyword))
                .willReturn(List.of(goods));

        //when
        List<GoodsResponse> goodsResponses = goodsService.goodsFindKeyword(keyword, pageable);

        //then
        assertThat(goodsResponses.size()).isEqualTo(1);
    }

//    @Test
//    @Disabled
//    @DisplayName("상품 수정")
//    void goodsEditTest(){
//        //given
//        Goods goods = GoodsFactory.createGoods();
//        Member member = MemberFactory.createMember();
//
//        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
//                .optionName("옵션이름")
//                .optionValue(null)
//                .totalPrice(12000)
//                .optionDescription("옵션설명").build();
//        Option option = Option.toOption(optionCreateRequest, goods);
//        GoodsEditRequest goodsEditRequest = GoodsEditRequest.builder()
//                .goodsName("상품이름")
//                .category(new Category("의류"))
//                .price(10000)
//                .optionCreateRequest(List.of(optionCreateRequest))
//                .goodsDescription("상품설명")
//                .build();
//
//        goods.update(goodsEditRequest);
//
//        List<String> impPaths = new ArrayList<>();
//        impPaths.add("5824bf15-d24f-4d34-97c7-47da110f9fc7.png");
//        Image image = Image.builder().fileUrl(impPaths.get(0)).goods(goods).build();
//
//        given(goodsRepository.findByIdAndMemberId(goods.getId(),member.getId())).willReturn(Optional.of(goods));
//        given(optionRepository.findAllByGoodsId(goods.getId())).willReturn(List.of(option));
//
//        //when
//        goodsService.goodsEdit(goods.getId(), member.getId(), goodsEditRequest, impPaths);
//
//        //then
//        verify(optionRepository).deleteById(option.getId());
//        verify(optionRepository).save(any());
//        for (String impPath : impPaths) {
//
//        }
//        verify(imageRepository).deleteById(image.getId());
//        verify(imageRepository).save(any());
//    }
//
//    @Test
//    @DisplayName("상품 삭제")
//    void goodsDeleteTest() {
//
//    }

}