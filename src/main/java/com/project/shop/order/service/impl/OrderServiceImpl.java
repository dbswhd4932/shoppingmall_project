package com.project.shop.order.service.impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.enetity.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.entity.Cart;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.order.domain.entity.Order;
import com.project.shop.order.domain.entity.OrderItem;
import com.project.shop.order.domain.entity.request.OrderCreateRequest;
import com.project.shop.order.domain.entity.response.OrderResponse;
import com.project.shop.order.repository.OrderItemRepository;
import com.project.shop.order.repository.OrderRepository;
import com.project.shop.order.service.OrderService;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final GoodsRepository goodsRepository;
    private final OrderItemRepository orderItemRepository;

    // 주문생성
    @Override
    public void createOrder(OrderCreateRequest orderCreateRequest, Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CART));

        Goods goods = goodsRepository.findById(cart.getGoodsId()).get();

        Order newOrder = Order.toOrder(orderCreateRequest, cart);
        orderRepository.save(newOrder);
        OrderItem orderItem = OrderItem.createOrderItem(orderCreateRequest.getMemberId(), goods,
                cart.getTotalPrice(), cart.getTotalAmount(), newOrder);

        orderItemRepository.save(orderItem);
    }

    // 주문 회원별 조회
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> orderFindMember(Long memberId) {
        return null;
    }

}
