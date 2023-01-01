package com.project.shop.member.controller;

import com.project.shop.factory.MemberFactory;
import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.repository.CategoryRepository;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.goods.repository.OptionRepository;
import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartResponse;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("장바구니 컨트롤러 통합테스트")
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
    CategoryRepository categoryRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        System.out.println("================== before 함수 호출 시작 ==================");
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Category category = Category.builder().category("의류").build();
        Goods goods = Goods.builder()
                .memberId(member.getId())
                .price(10000)
                .category(category)
                .goodsName("테스트상품")
                .description("상품설명")
                .build();
        memberRepository.save(member);
        categoryRepository.save(category);
        goodsRepository.save(goods);
        System.out.println("================== before 함수 호출 끝 ==================");
    }

    @Test
    @WithMockUser(username = "loginId", roles = "USER")
    @Transactional
    @DisplayName("장바구니 상품 추가")
    void cartAddGoods() throws Exception {
        //given
        Goods goods = goodsRepository.findByGoodsName("테스트상품").get();
        CartCreateRequest cartCreateRequest = CartCreateRequest.builder()
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
    @Transactional
    @DisplayName("장바구니 조회")
    void cartFind() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Cart cart = Cart.builder()
                .id(1L)
                .totalAmount(10)
                .goodsId(1L)
                .member(member)
                .totalPrice(10000)
                .optionNumber(1L)
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
        Member member = memberRepository.findById(1L).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        Cart cart = Cart.builder()
                .id(1L)
                .totalAmount(10)
                .goodsId(1L)
                .member(member)
                .totalPrice(10000)
                .optionNumber(1L)
                .build();
        cartRepository.save(cart);
        CartEditRequest cartEditRequest = CartEditRequest.builder()
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
        Member member = memberRepository.findByLoginId("loginId").get();
        Goods goods = goodsRepository.findByGoodsName("테스트상품").get();
        Cart cart = Cart.builder()
                .totalAmount(10)
                .goodsId(goods.getId())
                .member(member)
                .totalPrice(10000)
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