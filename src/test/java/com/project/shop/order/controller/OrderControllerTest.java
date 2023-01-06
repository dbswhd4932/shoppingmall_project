package com.project.shop.order.controller;

import com.project.shop.factory.MemberFactory;
import com.project.shop.factory.OrderFactory;
import com.project.shop.goods.domain.Category;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.repository.CategoryRepository;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.request.PayCancelRequest;
import com.project.shop.order.controller.response.OrderPageResponse;
import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderStatus;
import com.project.shop.order.domain.Pay;
import com.project.shop.order.repository.OrderRepository;
import com.project.shop.order.repository.PayRepository;
import com.project.shop.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@DisplayName("주문 컨트롤러 통합테스트")
class OrderControllerTest extends ControllerSetting {

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PayRepository payRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        System.out.println("================== before 함수 호출 시작 ==================");
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        memberRepository.save(member);
        Category category = Category.builder().category("의류").build();
        categoryRepository.save(category);
        Goods goods = Goods.builder()
                .memberId(member.getId())
                .price(10000)
                .category(category)
                .goodsName("테스트상품")
                .description("상품설명")
                .build();
        goodsRepository.save(goods);
        //todo 여기서 진행하면 무결성 참조오류
//        Cart cart = Cart.builder()
//                .totalAmount(10)
//                .totalPrice(100000)
//                .goodsId(goods.getId())
//                .member(member)
//                .build();
//        cartRepository.save(cart);
        System.out.println("================== before 함수 호출 끝 ==================");
    }

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
    @WithMockUser(username = "loginId", roles = "USER")
    @DisplayName("주문 생성")
    void orderCreate() throws Exception {
        //given
        Goods goods = goodsRepository.findByGoodsName("테스트상품").get();
        Member member = memberRepository.findByLoginId("loginId").get();
        OrderCreateRequest orderCreateRequest = OrderFactory.orderCreateRequest(goods);
        Cart cart = Cart.builder()
                .totalAmount(10)
                .totalPrice(100000)
                .goodsId(goods.getId())
                .member(member)
                .build();
        cartRepository.save(cart);

        //when
        mockMvc.perform(post("/api/orders")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderCreateRequest))
                        .with(user("loginId").roles("USER")))
                .andExpect(status().isCreated());

        //then
        assertThat(orderRepository.findAll().size()).isEqualTo(1);


    }

    @Test
    @WithMockUser(username = "loginId", roles = "USER")
    @DisplayName("주문 조회")
    void orderFindMember() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        Order order = OrderFactory.order(member);
        orderRepository.save(order);

        List<OrderPageResponse> orderPageRespons = orderService.orderFindMember(pageable);
        //when
        mockMvc.perform(get("/api/orders")
                        .with(user("loginId").roles("USER")))
                .andExpect(status().isOk());

        //then
        assertThat(orderPageRespons.size()).isEqualTo(1);

    }

    @Test
    @WithMockUser(username = "loginId", roles = "USER")
    @DisplayName("결제 취소")
    void orderCancel() throws Exception {
        //given
        Member member = memberRepository.findByLoginId("loginId").get();
        Order order = OrderFactory.order(member);
        Pay pay = Pay.builder()
                .order(order)
                .payPrice(1000)
                .cardCompany("국민")
                .cardNumber("1234-1234-1234-1234")
                .build();
        payRepository.save(pay);
        orderRepository.save(order);

        PayCancelRequest payCancelRequest
                = PayCancelRequest.builder().merchantId("1111").cancelReason("단순변심").build();

        //when
        mockMvc.perform(post("/api/payCancel")
                        .with(user("loginId").roles("USER"))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payCancelRequest)))
                .andExpect(status().isNoContent());

        //then
        assertThat(payRepository.findAll().size()).isEqualTo(1);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
    }

}