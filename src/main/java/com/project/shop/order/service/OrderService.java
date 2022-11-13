package com.project.shop.order.service;

import com.project.shop.member.domain.entity.Cart;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.order.domain.entity.request.OrderCreateRequest;
import com.project.shop.order.domain.entity.response.OrderResponse;

import java.util.List;

public interface OrderService {

    // 주문생성 - 장바구니에 있는 상품 구매
    void createOrder(OrderCreateRequest orderCreateRequest, Long cartId);

    // 주문 회원별 조회
    List<OrderResponse> orderFindMember(Long memberId);

    // 주문 수정 기능 X
    // 주문 삭제 기능 X
}