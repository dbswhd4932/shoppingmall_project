package com.project.shop.goods.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.GoodsEditRequest;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.controller.request.UpdateCheckRequest;
import com.project.shop.goods.controller.response.GoodsPageResponse;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.controller.response.UpdateGoodsResponse;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.OptionCreate;
import com.project.shop.goods.repository.CategoryRepository;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.goods.service.Impl.GoodsServiceImpl;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_MIXED_VALUE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ImportAutoConfiguration(WebSecurityConfig.class)
@DisplayName("?????? ???????????? ???????????????")
class GoodsControllerTest extends ControllerSetting {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    OptionRepository optionRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    GoodsServiceImpl goodsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Category category = Category.builder().category("??????").build();
        categoryRepository.save(category);
        memberRepository.save(member);
        Goods goods = Goods.builder()
                .memberId(member.getId())
                .price(10000)
                .category(category)
                .goodsName("???????????????")
                .description("????????????")
                .build();
        goodsRepository.save(goods);
    }

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("?????? ??????")
    void goodsCreate() throws Exception {
        //given
        Category category = categoryRepository.findByCategory("??????").get();
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(1000)
                .optionValue(List.of(optionCreate))
                .optionDescription("??????")
                .build();
        GoodsCreateRequest goodsCreateRequest = GoodsCreateRequest.builder()
                .goodsName("???????????????2")
                .categoryId(category.getId())
                .optionCreateRequest(List.of(optionCreateRequest))
                .price(10000)
                .goodsDescription("??????")
                .build();

        MockMultipartFile multipartFile =
                new MockMultipartFile("multipartFiles",
                        "????????????_20221219_050536.png",
                        "image/png", "????????????_20221219_050536.png"
                        .getBytes());
        String content = objectMapper.writeValueAsString(goodsCreateRequest);
        MockMultipartFile json = new MockMultipartFile("goodsCreateRequest", "goodsCreateRequest", "application/json", content.getBytes(StandardCharsets.UTF_8));

        //when
        mockMvc.perform(multipart("/api/goods")
                        .file(json)
                        .file(multipartFile)
                        .contentType(MULTIPART_MIXED_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                .with(user("loginId").roles("SELLER")))
                .andExpect(status().isCreated());

        //then
        assertThat(goodsRepository.findAll().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("?????? ?????? ??????")
    void goodsFindAll() throws Exception {
        //given
        //when
        mockMvc.perform(get("/api/goods"))
                .andExpect(status().isOk());

        //then
        assertThat(goodsRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ????????? ?????? ?????? true ??? ??????")
    void checkGoodsUpdateTrue() throws Exception {
        //given
        Goods goods = goodsRepository.findByGoodsName("???????????????").get();

        UpdateCheckRequest updateCheckRequest =
                UpdateCheckRequest.builder()
                        .goodsPrice(20000)
                        .goodsId(goods.getId())
                        .build();
        List<UpdateGoodsResponse> updateGoodsResponses =
                goodsService.checkGoodsUpdate(List.of(updateCheckRequest));

        //when
        mockMvc.perform(get("/api/goods/checkUpdateGoods")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(updateCheckRequest))))
                .andExpect(status().isOk());
        //then
        assertThat(updateGoodsResponses.get(0).isChangeCheck()).isTrue();
    }

    @Test
    @DisplayName("?????? ?????? ?????? ?????? - ????????? ?????? ?????? false ??? ??????")
    void checkGoodsUpdateFalse() throws Exception {
        //given
        Goods goods = goodsRepository.findByGoodsName("???????????????").get();

        UpdateCheckRequest updateCheckRequest =
                UpdateCheckRequest.builder()
                        .goodsPrice(10000)
                        .goodsId(goods.getId())
                        .build();
        List<UpdateGoodsResponse> updateGoodsResponses =
                goodsService.checkGoodsUpdate(List.of(updateCheckRequest));

        //when
        mockMvc.perform(get("/api/goods/checkUpdateGoods")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(updateCheckRequest))))
                .andExpect(status().isOk());
        //then
        assertThat(updateGoodsResponses.get(0).isChangeCheck()).isFalse();
    }

    @Test
    @DisplayName("?????? ?????? ??????")
    void goodsDetailFind() throws Exception {
        //given
        Goods goods = goodsRepository.findByGoodsName("???????????????").get();
        GoodsResponse goodsResponse = goodsService.goodsDetailFind(goods.getId());

        //when
        mockMvc.perform(get("/api/goods/{goodsId}", goods.getId())
                        .with(user("loginId")))
                .andExpect(status().isOk());

        //then
        assertThat(goodsResponse.getGoodsName()).isEqualTo("???????????????");
    }

    @Test
    @DisplayName("?????? ??????")
    void goodsFindKeyword() throws Exception {
        //given
        String keyword = "?????????";
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        List<GoodsPageResponse> goodsResponses = goodsService.goodsFindKeyword(keyword, pageable);

        //when
        mockMvc.perform(get("/api/goods/keyword")
                        .queryParam("keyword", keyword))
                .andExpect(status().isOk());

        //then
        assertThat(goodsResponses.get(0).getGoodsName()).isEqualTo("???????????????");


    }

    @Test
    @DisplayName("?????? ??????")
    void goodsEdit() throws Exception {
        //given
        Member findMember = memberRepository.findByLoginId("loginId").get();
        Goods goods = Goods.builder().goodsName("?????????").memberId(findMember.getId()).price(10000).build();
        Goods saveGoods = goodsRepository.save(goods);
        Category category = categoryRepository.findByCategory("??????").get();
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(1000)
                .optionValue(List.of(optionCreate))
                .optionDescription("??????")
                .build();
        GoodsEditRequest goodsEditRequest = GoodsEditRequest.builder().goodsName("test").categoryId(category.getId())
                .optionCreateRequest(List.of(optionCreateRequest))
                .price(20000)
                .goodsDescription("??????")
                .build();

        MockMultipartFile multipartFile =
                new MockMultipartFile("multipartFiles",
                        "????????????_20221219_050536.png",
                        "image/png", "????????????_20221219_050536.png"
                        .getBytes());
        String content = objectMapper.writeValueAsString(goodsEditRequest);
        MockMultipartFile json = new MockMultipartFile("goodsEditRequest", "goodsEditRequest", "application/json", content.getBytes(StandardCharsets.UTF_8));

        //when
        mockMvc.perform(multipart("/api/goods/{goodsId}" , saveGoods.getId())
                .file(json)
                .file(multipartFile)
                .contentType(MULTIPART_MIXED_VALUE)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(user("loginId").roles("SELLER")))
                .andExpect(status().isOk());

        //then
        assertThat(goods.getGoodsName()).isEqualTo("test");
        assertThat(goods.getPrice()).isEqualTo(20000);
    }

    @Test
    @DisplayName("?????? ??????")
    void goodsDelete() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Category category = categoryRepository.findByCategory("??????").get();
        Goods goods = Goods.builder()
                .memberId(member.getId())
                .price(10000)
                .category(category)
                .goodsName("???????????????2")
                .description("????????????")
                .build();
        goodsRepository.save(goods);
        Goods findGoods = goodsRepository.findByGoodsName("???????????????2").get();

        //when
        mockMvc.perform(delete("/api/goods/{goodsId}", findGoods.getId())
                        .with(user("loginId").roles("SELLER")))
                .andExpect(status().isNoContent());

        //then
        assertThat(goodsRepository.findAll().size()).isEqualTo(1);

    }
}