package com.project.shop.order.service.impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.order.domain.Order;
import com.project.shop.order.domain.OrderItem;
import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.response.OrderResponse;
import com.project.shop.order.domain.Pay;
import com.project.shop.order.repository.OrderItemRepository;
import com.project.shop.order.repository.OrderRepository;
import com.project.shop.order.repository.PayRepository;
import com.project.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final PayRepository payRepository;

    // 주문생성
    /*public void createOrder(Long cartId, OrderCreateRequest orderCreateRequest, PayCreateRequest payCreateRequest) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_CART));

        Order order = Order.toOrder(orderCreateRequest, cart);

        Pay pay = Pay.builder()
                .order(order)
                .cardCompany(payCreateRequest.getCardCompany())
                .cardNumber(payCreateRequest.getCardNumber())
                .payPrice(payCreateRequest.getPayPrice())
                .build();
        // todo 카드 사용 X 삭제 예정
        // 회원이 가진 카드를 조회
//        List<Card> cardList = cardRepository.findByMemberId(orderCreateRequest.getMemberId());
//        Pay pay = null;
        // 결제할 카드와 같으면 pay 생성
//        for (Card card : cardList) {
//            if (card.getId().equals(cardId)) {
//                pay = Pay.builder()
//                        .cardId(card.getId()).order(order).price(order.getTotalPrice()).build();
//                break;
//            }
//        }
//        if (pay == null) throw new BusinessException(ErrorCode.NOT_FOUND_CARD);

        // 주문 데이터 저장
        orderRepository.save(order);
        // 장바구니 상품 삭제
        cartRepository.deleteById(cartId);
        // 결제 데이터 저장
        payRepository.save(pay);

        Goods goods = goodsRepository.findById(cart.getGoodsId()).get();
        OrderItem orderItem = OrderItem.createOrderItem(orderCreateRequest.getMemberId(), goods,
                cart.getTotalPrice(), cart.getTotalAmount(), order);
        // 주문_상품 데이터 저장
        orderItemRepository.save(orderItem);
    }*/

    // 장바구니 상품 전체 주문 생성
    @Override
    public void buyAll(OrderCreateRequest orderCreateRequest) {
        List<Cart> carts = cartRepository.findByMemberId(orderCreateRequest.getMemberId()).orElseThrow(
                () -> new BusinessException(ErrorCode.CART_NO_PRODUCTS));

        int payTotalPrice = 0;
        for (Cart cart : carts) {
            payTotalPrice += cart.getTotalPrice();
        }

        // 주문_상품 DB 저장
        Order order = Order.toOrder(orderCreateRequest, payTotalPrice);
        for (Cart cart : carts) {
            Goods goods = goodsRepository.findById(cart.getGoodsId()).orElseThrow(
                    () -> new BusinessException(ErrorCode.NOT_FOUND_GOODS));
            OrderItem orderItem = OrderItem.createOrderItem(orderCreateRequest.getMemberId(), goods, cart.getTotalPrice(), cart.getTotalAmount(), order);
            orderItemRepository.save(orderItem);
        }

        // 주문 DB 저장
        orderRepository.save(order);

        Pay pay = Pay.builder()
                .cardCompany(orderCreateRequest.getCardCompany())
                .cardNumber(orderCreateRequest.getCardNumber())
                .order(order)
                .payPrice(payTotalPrice)
                .build();

        payRepository.save(pay);
    }

    // 주문 회원별 조회 - 여러 주문이 있을 수 있다.
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> orderFindMember(Long memberId, Pageable pageable) {
        Page<Order> orderList = orderRepository.findAll(pageable);
        List<OrderResponse> list = new ArrayList<>();
        for (Order order : orderList) {
            if (order.getMemberId().equals(memberId)) {
                list.add(OrderResponse.toOrderResponse(order));
            }
        }
        return list;
    }
}
