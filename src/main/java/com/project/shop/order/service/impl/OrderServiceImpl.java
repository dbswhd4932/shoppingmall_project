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

import java.util.ArrayList;
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

        // 추가 주문일 경우 금액 추가하고 리턴
        List<Order> orderList = orderRepository.findAll();
        for (Order order : orderList) {
            if ( order.getMemberId().equals(orderCreateRequest.getMemberId())) {
                order.addTotalPrice(cart.getTotalPrice());
                // 추가 후 장바구니 상품 삭제
                cartRepository.deleteById(cartId);
                return;
            }
        }

        // 추가 주문이 아니면 새로 생성
        if (cart.getMember().getId().equals(orderCreateRequest.getMemberId())) {
            Order newOrder = Order.toOrder(orderCreateRequest, cart);

            orderRepository.save(newOrder);
            // 추가 후 장바구니 상품 삭제
            cartRepository.deleteById(cartId);

            Goods goods = goodsRepository.findById(cart.getGoodsId()).get();
            OrderItem orderItem = OrderItem.createOrderItem(orderCreateRequest.getMemberId(), goods,
                    cart.getTotalPrice(), cart.getTotalAmount(), newOrder);

            orderItemRepository.save(orderItem);
        } else {
            throw new BusinessException(ErrorCode.NOT_FOUND_GOODS);
        }
    }

    // 주문 회원별 조회 - 여러 주문이 있을 수 있다.
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> orderFindMember(Long memberId) {
        List<Order> orderList = orderRepository.findAll();
        List<OrderResponse> list = new ArrayList<>();
        for (Order order : orderList) {
            if ( order.getMemberId().equals(memberId)) {
                list.add(OrderResponse.toOrderResponse(order));
            }
        }
        return list;
    }
}
