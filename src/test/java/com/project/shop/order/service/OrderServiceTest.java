package com.project.shop.order.service;

import com.project.shop.factory.GoodsFactory;
import com.project.shop.factory.MemberFactory;
import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.domain.request.CreateOrderRequestDto;
import com.project.shop.order.repository.OrderItemRepository;
import com.project.shop.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    MemberRepository memberRepository;

    @Mock
    GoodsRepository goodsRepository;

    @Mock
    OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("상품 주문 테스트")
    void createOrderTest() {
        Member member = MemberFactory.createMember();
        Goods goods = GoodsFactory.createGoods();

        CreateOrderRequestDto requestDto = CreateOrderRequestDto.builder()
                .member(member)
                .goods(goods)
                .name("홍길동")
                .phone("010-1234-1234")
                .address(new Address("zipcode", "detail"))
                .requirement("요구사항")
                .build();

        orderService.order(requestDto);

        // todo assert 테스트는 어떻게 ??....
    }
}