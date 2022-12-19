package com.project.shop.order.service;

import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.request.PayCancelRequest;
import com.project.shop.order.controller.response.OrderResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    // todo 삭제 확인
    // 주문생성 - 장바구니에 있는 상품 구매
//    void createOrder(Long cartId, OrderCreateRequest orderCreateRequest, PayCreateRequest payCreateRequest);

    // 주문 생성
    void cartOrder(OrderCreateRequest orderCreateRequest);

    // 주문 조회
    List<OrderResponse> orderFindMember(Pageable pageable);

    // 가맹점 ID 조회
    public String findMerchantId(Long orderId);

    // 결제 취소
    public void payCancel(Long payId, PayCancelRequest payCancelRequest);

}
