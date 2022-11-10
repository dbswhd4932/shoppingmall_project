package com.project.shop.order.service;


import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.domain.entity.Order;
import com.project.shop.order.domain.request.CreateOrderRequestDto;
import com.project.shop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final GoodsRepository goodsRepository;

    /**
     *  주문생성
     */
    public void order(CreateOrderRequestDto createOrderRequestDto) {
        Order order = Order.createOrder(createOrderRequestDto);

        orderRepository.save(order);

    }

}
