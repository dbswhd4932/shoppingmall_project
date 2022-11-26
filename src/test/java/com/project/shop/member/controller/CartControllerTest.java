package com.project.shop.member.controller;

import com.project.shop.factory.MemberFactory;
import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.response.CartResponse;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.CartService;
import com.project.shop.member.service.Impl.CartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@MockBean(JpaMetamodelMappingContext.class)
class CartControllerTest extends ControllerSetting{

    @MockBean
    CartServiceImpl cartService;

    @MockBean
    CartRepository cartRepository;

    @Test
    @DisplayName("장바구니 담기")
    void cartAddGoodsTest() throws Exception {
        //given
        CartCreateRequest cartCreateRequest = CartCreateRequest.builder()
                .goodsId(1L)
                .amount(5)
                .build();

        //when then
        mockMvc.perform(post("/api/carts")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartCreateRequest)))
                .andExpect(status().isCreated());

        verify(cartService).cartAddGoods(refEq(cartCreateRequest),any());
    }

    @Test
    @DisplayName("장바구니 조회")
    void cartFindTest() throws Exception {
        //given
        Member member = MemberFactory.createMember();
        Cart cart = Cart.builder()
                .member(member)
                .goodsId(1L)
                .totalAmount(5)
                .totalPrice(5000)
                .build();

        CartResponse cartResponse = CartResponse.builder()
                .memberId(1L)
                .goodsId(2L)
                .totalAmount(5)
                .totalPrice(5000)
                .build();

        given(cartRepository.findByMemberId(cart.getMember().getId())).willReturn(Optional.of(List.of(cart)));
        given(cartService.cartFindMember(cart.getMember().getId())).willReturn(List.of(cartResponse));

        //when then
        mockMvc.perform(get("/api/carts")
                .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].totalAmount").value(5))
                .andExpect(jsonPath("$.[0].totalPrice").value(5000))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("장바구니 상품 삭제")
    void cartDeleteGoodsTest() throws Exception {
        //given
        //when then
        mockMvc.perform(delete("/api/carts/{cartId}", 1L)
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cartService).cartDeleteGoods(any(), any());

    }


}