package com.project.shop.order.service;

import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.response.OrderResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    // todo 삭제 확인
    // 주문생성 - 장바구니에 있는 상품 구매
//    void createOrder(Long cartId, OrderCreateRequest orderCreateRequest, PayCreateRequest payCreateRequest);

    // 장바구니 상품 전체 구매
    void buyAll(OrderCreateRequest orderCreateRequest);

    // 주문 회원별 조회
    List<OrderResponse> orderFindMember(Long memberId, Pageable pageable);

    // 주문 수정 기능 X
    // 주문 삭제 기능 X
}
