package com.project.shop.order.service.impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.goods.domain.Goods;
import com.project.shop.goods.repository.GoodsRepository;
import com.project.shop.member.domain.Cart;
import com.project.shop.member.domain.Member;
import com.project.shop.member.repository.CartRepository;
import com.project.shop.member.repository.MemberRepository;
import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.request.PayCancelRequest;
import com.project.shop.order.controller.response.OrderPageResponse;
import com.project.shop.order.controller.response.OrderResponse;
import com.project.shop.order.domain.*;
import com.project.shop.order.repository.OrderItemRepository;
import com.project.shop.order.repository.OrderRepository;
import com.project.shop.order.repository.PayCancelRepository;
import com.project.shop.order.repository.PayRepository;
import com.project.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.project.shop.global.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final GoodsRepository goodsRepository;
    private final PayRepository payRepository;
    private final OrderItemRepository orderItemRepository;
    private final MemberRepository memberRepository;
    private final PayCancelRepository payCancelRepository;

    // 주문 생성
    @Override
    public void cartOrder(OrderCreateRequest orderCreateRequest) {

        Member member = getMember();

        Order order = Order.toOrder(orderCreateRequest, member);

        // 주문_상품 DB 저장
        for (OrderCreateRequest.orderItemCreate orderItemCreate : orderCreateRequest.getOrderItemCreates()) {
            Goods goods = goodsRepository.findById(orderItemCreate.getGoodsId()).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_GOODS));
            OrderItem orderItem =
                    OrderItem.createOrderItem(member, goods.getId(), orderItemCreate.getOrderPrice(), orderItemCreate.getAmount(),
                            order, goods.getGoodsName(), orderItemCreate.getOrderPrice()/orderItemCreate.getAmount());
            orderItemRepository.save(orderItem);

            // 장바구니에 없는 상품이면 예외처리
            Cart cart = cartRepository.findByGoodsIdAndMember(goods.getId(), member).orElseThrow(
                    () -> new BusinessException(CART_NO_PRODUCTS));

            // 주문된 상품은 장바구니에서 삭제
            cartRepository.deleteById(cart.getId());
        }

        // 주문 DB 저장
        orderRepository.save(order);

        Pay pay = Pay.builder()
                .cardCompany(orderCreateRequest.getCardCompany())
                .cardNumber(orderCreateRequest.getCardNumber())
                .order(order)
                .payPrice(order.getTotalPrice())
                .build();

        // 결제 DB 저장
        payRepository.save(pay);

    }

    // 주문 조회
    @Override
    @Transactional(readOnly = true)
    public List<OrderPageResponse> orderFindMember(Pageable pageable) {
        Member member = getMember();
        Page<Order> orderList = orderRepository.findAll(pageable);

        List<OrderPageResponse> orderPageResponseList = orderList.stream().filter(findOrder -> findOrder.getMemberId().equals(member.getId()))
                .map(order -> OrderPageResponse.toResponse(order, orderList)).toList();

        return orderPageResponseList;
    }

    // 주문 단건 조회
    @Override
    public OrderResponse orderFindOne(Long orderId) {
        Member member = getMember();
        Order order = orderRepository.findByIdAndMemberId(orderId, member.getId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_ORDERS));

        OrderResponse orderResponse = OrderResponse.toResponse(order);
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(order);
        List<Long> list = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {
            list.add(orderItem.getGoodsId());
        }
        orderResponse.setGoodsId(list);
        return orderResponse;
    }

    // 결제취소
    @Override
    public void payCancel(PayCancelRequest payCancelRequest) {
        Member member = getMember();

        Order order = orderRepository.findByMerchantId(payCancelRequest.getMerchantId())
                .orElseThrow(() -> new BusinessException(NOT_EQUAL_MERCHANT_ID));


        Pay pay = payRepository.findByOrderId(order.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.NOT_FOUND_PAY));

        // 결제 회원과 로그인한 회원이 다르면 예외처리
        if(!pay.getMemberId().equals(member.getId())) {
            throw new BusinessException(CAN_NOT_CANCEL_PAY);
        }

        // 이미 취소된 결제는 예외처리
        if (pay.getPayStatus().equals(PayStatus.CANCEL)) {
            throw new BusinessException(ALREADY_CANCEL_PAY);
        }

        PayCancel payCancel = PayCancel.builder()
                .order(pay.getOrder())
                .merchantId(payCancelRequest.getMerchantId())
                .cancelReason(payCancelRequest.getCancelReason())
                .cancelPrice(pay.getPayPrice())
                .cardCompany(pay.getCardCompany())
                .cardNumber(pay.getCardNumber())
                .build();

        // ORDER_ITEM DB 상품 삭제
        List<OrderItem> orderItems = orderItemRepository.findAllByOrder(pay.getOrder());
        orderItemRepository.deleteAll(orderItems);

        // 결제취소 DB 저장
        payCancelRepository.save(payCancel);
        // 결제 DB 상태 변경
        pay.PayStatusChangeCancel();
        // 주문 DB 상태 변경
        pay.getOrder().orderStatusChangeCancel();
    }

    private Member getMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = authentication.getName();

        Member member = memberRepository.findByLoginId(loginId).orElseThrow(() -> new BusinessException(NOT_FOUND_MEMBER));
        return member;
    }
}
