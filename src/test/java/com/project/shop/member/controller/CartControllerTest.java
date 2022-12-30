package com.project.shop.member.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.controller.request.OptionCreateRequest;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.domain.Option;
import com.project.shop.goods.domain.OptionCreate;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartResponse;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ImportAutoConfiguration(WebSecurityConfig.class)
@WebAppConfiguration
@DisplayName("컨트롤러 장바구니 통합테스트")
class CartControllerTest extends ControllerSetting {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OptionRepository optionRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    WebApplicationContext context;

    @Autowired
    PasswordEncoder passwordEncoder;

//    @BeforeEach
//    void beforeEach() {
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .alwaysDo(print())
//                .apply(springSecurity())
//                .build();
//    }

    @Test
    @WithMockUser(username = "loginId", roles = "USER")
    @DisplayName("장바구니 상품 추가")
    void cartAddGoods() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(1000)
                .optionValue(List.of(optionCreate))
                .optionDescription("설명")
                .build();
        Goods goods = GoodsFactory.createGoods();
        goodsRepository.save(goods);
        Option option = Option.toOption(optionCreateRequest, goods);
        CartCreateRequest cartCreateRequest = CartCreateRequest.builder()
                .optionNumber(option.getId())
                .amount(10)
                .goodsId(goods.getId())
                .build();

        //when
        mockMvc.perform(post("/api/carts")
                        .with(user("loginId").roles("USER"))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartCreateRequest)))
                .andExpect(status().isCreated());

        //then
        assertThat(cartRepository.findAll().size()).isEqualTo(1);
    }

    @Test
    @WithMockUser(username = "loginId", roles = "USER")
    @DisplayName("장바구니 조회")
    void cartFind() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(1000)
                .optionValue(List.of(optionCreate))
                .optionDescription("설명")
                .build();
        Goods goods = GoodsFactory.createGoods();
        goodsRepository.save(goods);
        Option option = Option.toOption(optionCreateRequest, goods);
        Cart cart = Cart.builder()
                .id(1L)
                .totalAmount(10)
                .goodsId(goods.getId())
                .member(member)
                .totalPrice(10000)
                .optionNumber(option.getId())
                .build();
        cartRepository.save(cart);
        CartResponse cartResponse = CartResponse.toResponse(cart);

        //when
        mockMvc.perform(get("/api/carts")
                        .with(user("loginId").roles("USER")))
                .andExpect(status().isOk());

        //then
        assertThat(cartResponse.getTotalAmount()).isEqualTo(10);
    }

    @Test
    @WithMockUser(username = "loginId", roles = "USER")
    @DisplayName("장바구니 수정")
    void cartEdit() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(1000)
                .optionValue(List.of(optionCreate))
                .optionDescription("설명")
                .build();
        Goods goods = GoodsFactory.createGoods();
        goodsRepository.save(goods);
        Option option = Option.toOption(optionCreateRequest, goods);
        Cart cart = Cart.builder()
                .totalAmount(10)
                .goodsId(goods.getId())
                .member(member)
                .totalPrice(10000)
                .optionNumber(option.getId())
                .build();
        cartRepository.save(cart);
        CartEditRequest cartEditRequest = CartEditRequest.builder()
                .optionNumber(option.getId())
                .amount(20)
                .build();

        //when
        mockMvc.perform(put("/api/carts/{cartId}", cart.getId())
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartEditRequest))
                .with(user("loginId").roles("USER")))
                .andExpect(status().isOk());
        //then
        assertThat(cartRepository.findById(cart.getId()).get().getTotalAmount()).isEqualTo(20);

    }

    @Test
    @WithMockUser(username = "loginId", roles = "USER")
    @DisplayName("장바구니 상품 삭제")
    void cartDeleteGoods() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        OptionCreate optionCreate = OptionCreate.builder().key("key").value("value").build();
        OptionCreateRequest optionCreateRequest = OptionCreateRequest.builder()
                .totalPrice(1000)
                .optionValue(List.of(optionCreate))
                .optionDescription("설명")
                .build();
        Goods goods = GoodsFactory.createGoods();
        goodsRepository.save(goods);
        Option option = Option.toOption(optionCreateRequest, goods);
        Cart cart = Cart.builder()
                .totalAmount(10)
                .goodsId(goods.getId())
                .member(member)
                .totalPrice(10000)
                .optionNumber(option.getId())
                .build();
        cartRepository.save(cart);

        //when
        mockMvc.perform(delete("/api/carts/{cartId}", cart.getId())
                .with(user("loginId").roles("USER")))
                .andExpect(status().isNoContent());
        //then
        assertThat(cartRepository.findAll().size()).isEqualTo(0);
    }

}