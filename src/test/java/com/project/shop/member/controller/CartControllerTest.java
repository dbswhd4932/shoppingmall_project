package com.project.shop.member.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.member.controller.request.CartCreateRequest;
import com.project.shop.member.controller.request.CartEditRequest;
import com.project.shop.member.controller.response.CartResponse;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.service.Impl.CartServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ImportAutoConfiguration(WebSecurityConfig.class)
@DisplayName("장바구니 컨트롤러 테스트")
class CartControllerTest extends ControllerSetting {

    @MockBean
    CartServiceImpl cartService;

    @MockBean
    CartRepository cartRepository;

    @Test
    @DisplayName("장바구니 담기")
    @WithMockUser(roles = "USER")
    void cartAddGoodsTest() throws Exception {
        //given
        CartCreateRequest cartCreateRequest
                = CartCreateRequest.builder().goodsId(1L).amount(5).build();

        //when
        mockMvc.perform(post("/api/carts")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cartCreateRequest)))
                .andExpect(status().isCreated());

        //then
        verify(cartService).cartAddGoods(refEq(cartCreateRequest));
    }

    @Test
    @DisplayName("장바구니 조회")
    @WithMockUser(roles = "USER")
    void cartFindTest() throws Exception {
        //given
        CartResponse cartResponse
                = CartResponse.builder().memberId(1L).goodsId(1L).totalPrice(1000).totalAmount(10).build();
        given(cartService.cartFindMember()).willReturn(List.of(cartResponse));

        //when then
        mockMvc.perform(get("/api/carts")
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].totalAmount").value(10))
                .andExpect(jsonPath("$.[0].totalPrice").value(1000))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("장바구니 수정")
    @WithMockUser(roles = "USER")
    void cartEdit() throws Exception {
        //given
        CartEditRequest cartEditRequest
                = CartEditRequest.builder().amount(1000).optionNumber(1L).build();

        //when
        mockMvc.perform(put("/api/carts/{cartId}", 1L)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cartEditRequest)))
                .andExpect(status().isOk());

        //then
        verify(cartService).editCartItem(anyLong(), refEq(cartEditRequest));
    }

    @Test
    @DisplayName("장바구니 삭제")
    @WithMockUser(roles = "USER")
    void cartDeleteGoodsTest() throws Exception {
        //given
        //when
        mockMvc.perform(delete("/api/carts/{cartId}", 1L))
                .andExpect(status().isNoContent());

        //then
        verify(cartService).cartDeleteGoods(1L);

    }

}