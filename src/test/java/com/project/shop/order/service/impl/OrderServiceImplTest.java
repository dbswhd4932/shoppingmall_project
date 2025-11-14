package com.project.shop.order.service.impl;

import com.project.shop.factory.CartFactory;
import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.factory.OrderFactory;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.member.service.RedisCartService;
import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.request.PayCancelRequest;
import com.project.shop.order.controller.response.OrderPageResponse;
import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderItem;
import com.project.shop.order.domain.Pay;
import com.project.shop.order.domain.PayCancel;
import com.project.shop.order.repository.OrderItemRepository;
import com.project.shop.order.repository.OrderRepository;
import com.project.shop.order.repository.PayCancelRepository;
import com.project.shop.order.repository.PayRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 서비스 테스트")
class OrderServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    CartRepository cartRepository;

    @Mock
    PayRepository payRepository;

    @Mock
    OrderItemRepository orderItemRepository;

    @Mock
    PayCancelRepository payCancelRepository;

    @Mock
    GoodsRepository goodsRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    RedisCartService redisCartService;

    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach()
    void beforeEach() {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        TestingAuthenticationToken mockAuthentication = new TestingAuthenticationToken("loginId", "1234");
        context.setAuthentication(mockAuthentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("주문 생성")
    void cartOrder() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();
        Order order = OrderFactory.order(member);
        Cart cart = CartFactory.cartCreate(member, goods);
        OrderItem orderItem = OrderItem.createOrderItem
                (member, goods.getId(), 10000, 10, order, goods.getGoodsName(), goods.getPrice());
        OrderCreateRequest orderCreateRequest = OrderFactory.orderCreateRequest(goods);
        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(goodsRepository.findById(orderCreateRequest.getOrderItemCreates().get(0).getGoodsId()))
                .willReturn(Optional.of(goods));

        given(orderItemRepository.save(any())).willReturn(orderItem);
        // Redis 장바구니 사용으로 cartRepository Mock 제거

        //when
        orderService.cartOrder(orderCreateRequest);

        //then
        // Redis 장바구니에서 삭제되는지 확인
        verify(redisCartService).removeFromCart(any(), any());
        verify(orderRepository).save(any());
        verify(payRepository).save(any());
    }

    @Test
    @DisplayName("주문 조회")
    void orderFindMember() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Order order = OrderFactory.order(member);
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        List<Order> list = new ArrayList<>();
        list.add(order);

        PageImpl<Order> orders = new PageImpl<>(list);

        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(orderRepository.findAll(pageable)).willReturn(orders);

        //when
        List<OrderPageResponse> orderPageRespons = orderService.orderFindMember(pageable);

        //then
        assertThat(orderPageRespons.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("주문 취소")
    void payCancel() {
        //given
        MemberFactory memberFactory = new MemberFactory(passwordEncoder);
        Member member = memberFactory.createMember();
        Order order = OrderFactory.order(member);
        Pay pay = new Pay("cardCompany", "1111-1111-1111-1111", order, 10000);
        PayCancel payCancel = new PayCancel(order, "1111", "reason", 10000, "cardCompany", "1111-1111-1111-1111");
        PayCancelRequest payCancelRequest = new PayCancelRequest("1111", "reason");

        given(memberRepository.findByLoginId(member.getLoginId())).willReturn(Optional.of(member));
        given(orderRepository.findByMerchantId(payCancelRequest.getMerchantId())).willReturn(Optional.of(order));
        given(payRepository.findByOrderId(order.getId())).willReturn(Optional.of(pay));

        //when
        orderService.payCancel(payCancelRequest);

        //then
        verify(payCancelRepository).save(any());

    }

}