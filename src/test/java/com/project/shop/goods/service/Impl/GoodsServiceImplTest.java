package com.project.shop.goods.service.Impl;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.controller.request.UpdateCheckRequest;
import com.project.shop.goods.controller.response.GoodsPageResponse;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.controller.response.UpdateGoodsResponse;
import com.project.shop.goods.domain.*;
import com.project.shop.goods.repository.CategoryRepository;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.ImageRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("상품 서비스 테스트")
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
    MemberRepository memberRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    S3Service s3Service;


    @BeforeEach()
    void beforeEach() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TestingAuthenticationToken mockAuthentication = new TestingAuthenticationToken("loginId", "1234");
        context.setAuthentication(mockAuthentication);
        SecurityContextHolder.setContext(context);
    }


    @Test
    @DisplayName("상품 등록")
    void goodsCreate() throws IOException {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Category category = Category.builder().category("의류").build();
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(10000).optionValue(List.of(optionCreate)).optionDescription("설명").build();
        GoodsCreateRequest goodsCreateRequest = GoodsCreateRequest.builder().goodsName("상품이름").
                optionCreateRequest(List.of(optionCreateRequest)).build();

        List<String> list = s3Service.upload(any());
        for (String img : list) {
            Image image = Image.builder().fileUrl(img).goods(goods).build();
            given(imageRepository.save(image)).willReturn(image);
            verify(imageRepository).save(image);
        }
        given(memberRepository.findByLoginId(any())).willReturn(Optional.ofNullable(member));
        given(categoryRepository.findById(category.getId())).willReturn(Optional.of(category));

        goodsService.goodsCreate(goodsCreateRequest, any());

        //then
        verify(goodsRepository).save(any());
        verify(optionRepository).saveAll(any());
        verify(imageRepository).saveAll(any());
    }

    @Test
    @DisplayName("상품 전체 조회")
    void goodsFindAll() {
        //given
        Goods goods = GoodsFactory.createGoods();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        List<Goods> goodsList = new ArrayList<>();
        goodsList.add(goods);

        PageImpl<Goods> goodsPage = new PageImpl<>(goodsList);
        given(goodsRepository.findAll(pageable)).willReturn(goodsPage);

        //when
        List<GoodsPageResponse> goodsResponses = goodsService.goodsFindAll(pageable);

        //then
        assertThat(goodsResponses.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("상품 가격 변경 확인")
    void checkGoodsUpdate() {
        //given
        Goods goods = GoodsFactory.createGoods();
        UpdateCheckRequest updateCheckRequest = UpdateCheckRequest.builder().goodsId(goods.getId()).goodsPrice(2000).build();
        given(goodsRepository.findById(goods.getId())).willReturn(Optional.of(goods));

        //when
        List<UpdateGoodsResponse> goodsResponses = goodsService.checkGoodsUpdate(List.of(updateCheckRequest));

        //then
        assertThat(goodsResponses.get(0).getGoodsPrice()).isEqualTo(2000);

    }

    @Test
    @DisplayName("상품 상세 조회")
    void goodsDetailFind() {
        //given
        Goods goods = GoodsFactory.createGoods();
        given(goodsRepository.findById(any())).willReturn(Optional.ofNullable(goods));

        //when
        GoodsResponse goodsResponse = goodsService.goodsDetailFind(goods.getId());

        //then
        assertThat(goodsResponse.getGoodsName()).isEqualTo("테스트상품");
    }

    @Test
    @DisplayName("상품 검색")
    void goodsFindKeyword() {
        //given
        String keyword = "테스트";
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        Goods goods = GoodsFactory.createGoods();
        List<Goods> list = new ArrayList<>();
        list.add(goods);
        PageImpl<Goods> page = new PageImpl<>(list);

        given(goodsRepository.findGoodsByGoodsNameContaining(pageable, keyword)).willReturn(page);
        //when
        List<GoodsPageResponse> goodsResponses = goodsService.goodsFindKeyword(keyword, pageable);

        //then
        assertThat(goodsResponses.get(0).getGoodsName()).isEqualTo("테스트상품");
    }

    @Test
    @DisplayName("상품 수정")
    void goodsEdit() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(10000).optionValue(List.of(optionCreate)).optionDescription("설명").build();
        Options options = Options.toOption(optionCreateRequest, goods);
        GoodsEditRequest goodsEditRequest = new GoodsEditRequest("name", 1L, 3000, List.of(optionCreateRequest), "설명");

        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(goodsRepository.findById(goods.getId())).willReturn(Optional.of(goods));
        given(optionRepository.findByGoodsId(goods.getId())).willReturn(List.of(options));

        List<String> list = s3Service.upload(any());
        for (String img : list) {
            Image image = Image.builder().fileUrl(img).goods(goods).build();
            given(imageRepository.save(image)).willReturn(image);
            verify(imageRepository).save(image);
        }

        //when
        goodsService.goodsEdit(goods.getId(), goodsEditRequest, any());

        //then
        verify(optionRepository).deleteAll(any());
        verify(optionRepository).saveAll(any());
        verify(imageRepository).saveAll(any());
    }

    @Test
    @DisplayName("상품 삭제")
    void goodsDelete() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();

        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.ofNullable(member));
        given(goodsRepository.findById(goods.getId())).willReturn(Optional.of(goods));

        //when
        goodsService.goodsDelete(goods.getId());

        //then
        verify(goodsRepository).deleteById(goods.getId());

    }
}