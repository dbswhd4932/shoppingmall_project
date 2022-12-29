package com.project.shop.order.controller;

import com.project.shop.config.WebSecurityConfig;
import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.factory.OrderFactory;
import com.project.shop.goods.domain.Goods;
import com.project.shop.member.domain.Member;
import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.request.PayCancelRequest;
import com.project.shop.order.controller.response.OrderResponse;
import com.project.shop.order.domain.Order;
import com.project.shop.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@MockBean(JpaMetamodelMappingContext.class)
@ImportAutoConfiguration(WebSecurityConfig.class)
@DisplayName("주문 컨트롤러 테스트")
class OrderControllerTest extends ControllerSetting {

    @MockBean
    OrderServiceImpl orderService;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("주문번호 ID 생성")
    void merchantIdCreate() throws Exception {
        //given
        //when
        mockMvc.perform(get("/api/merchantId"))
                .andExpect(status().isCreated());
        //then
    }

    @Test
    @DisplayName("주문 생성")
    void orderCreate() throws Exception {
        //given
        Goods goods = GoodsFactory.createGoods();
        OrderCreateRequest orderCreateRequest = OrderFactory.orderCreateRequest(goods);

        //when
        mockMvc.perform(post("/api/orders")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderCreateRequest)))
                .andExpect(status().isCreated());

        //then
        verify(orderService).cartOrder(refEq(orderCreateRequest));
    }

    @Test
    @DisplayName("주문 조회")
    void orderFindMember() throws Exception {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Order order = OrderFactory.order(member);
        OrderResponse orderResponse = OrderResponse.toOrderResponse(order);
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        given(orderService.orderFindMember(pageable)).willReturn(List.of(orderResponse));

        //when then
        mockMvc.perform(get("/api/orders"))
                .andExpect(jsonPath("$.[0].name").value("name"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("결제 취소")
    void orderCancel() throws Exception {
        //given
        PayCancelRequest payCancelRequest
                = PayCancelRequest.builder().merchantId("1111").cancelReason("단순변심").build();

        //when
        mockMvc.perform(post("/api/payCancel")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payCancelRequest)))
                .andExpect(status().isNoContent());

        //then
        verify(orderService).payCancel(refEq(payCancelRequest));
    }

}