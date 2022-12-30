package com.project.shop.goods.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.factory.GoodsFactory;
import com.project.shop.goods.controller.request.GoodsCreateRequest;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.controller.request.UpdateCheckRequest;
import com.project.shop.goods.controller.response.GoodsResponse;
import com.project.shop.goods.controller.response.UpdateGoodsResponse;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Option;
import com.project.shop.goods.domain.OptionCreate;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.OptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ImportAutoConfiguration(WebSecurityConfig.class)
@DisplayName("상품 컨트롤러 통합테스트")
class GoodsControllerTest extends ControllerSetting {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    OptionRepository optionRepository;

    @Test
    @WithMockUser(roles = "SELLER")
    @DisplayName("상품 등록")
    //todo multipartFile ???
    void goodsCreate() throws Exception {
        //given
        Category category = Category.builder().category("의류").build();
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(1000)
                .optionValue(List.of(optionCreate))
                .optionDescription("설명")
                .build();
        GoodsCreateRequest goodsCreateRequest = GoodsCreateRequest.builder()
                .optionCreateRequest(List.of(optionCreateRequest))
                .category(category)
                .goodsName("goodsName")
                .price(1000)
                .goodsDescription("description")
                .build();

        //when
        mockMvc.perform(post("/api/goods")
                        .with(user("loginId").roles("SELLER"))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goodsCreateRequest)))
                .andExpect(status().isCreated());
        //then
    }

    @Test
    @DisplayName("상품 전체 조회")
    void goodsFindAll() throws Exception {
        //given
        Goods goods = GoodsFactory.createGoods();
        goodsRepository.save(goods);
        GoodsResponse goodsResponse = GoodsResponse.toResponse(goods);

        //when
        mockMvc.perform(get("/api/goods"))
                .andExpect(status().isOk());

        //then
        assertThat(goodsResponse.getGoodsName()).isEqualTo("테스트상품");
    }

    @Test
    @DisplayName("상품 가격 변경 확인")
    void checkGoodsUpdate() throws Exception {
        //given
        Goods goods = GoodsFactory.createGoods();
        goodsRepository.save(goods);
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(1000)
                .optionValue(List.of(optionCreate))
                .optionDescription("설명")
                .build();
        Option option = Option.toOption(optionCreateRequest, goods);
        optionRepository.save(option);
        UpdateCheckRequest updateCheckRequest =
                UpdateCheckRequest.builder()
                        .optionId(option.getId())
                        .goodsPrice(1000)
                        .goodsId(goods.getId())
                        .build();

        UpdateGoodsResponse updateGoodsResponse = UpdateGoodsResponse.builder()
                .goodsId(goods.getId())
                .goodsPrice(1000)
                .changeCheck(true)
                .build();

        //when
        mockMvc.perform(get("/api/goods/checkUpdateGoods")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(updateCheckRequest))))
                .andExpect(status().isOk());
        //then
        assertThat(updateGoodsResponse.isChangeCheck()).isTrue();
    }

    @Test
    void goodsDetailFind() {
    }

    @Test
    void goodsFindKeyword() {
    }

    @Test
    void goodsEdit() {
    }

    @Test
    void goodsDelete() {
    }
}