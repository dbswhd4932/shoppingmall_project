package com.project.shop.order.service;

import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.request.PayCancelRequest;
import com.project.shop.order.controller.response.OrderResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    // 주문 생성
    void cartOrder(OrderCreateRequest orderCreateRequest);

    // 주문 조회
    List<OrderResponse> orderFindMember(Pageable pageable);

    // 결제 취소
    public void payCancel(PayCancelRequest payCancelRequest);

}
