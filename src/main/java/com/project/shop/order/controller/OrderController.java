package com.project.shop.order.controller;

import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.response.OrderResponse;
import com.project.shop.order.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderServiceImpl orderService;

    //주문 생성
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public void orderCreate(@RequestBody OrderCreateRequest orderCreateRequest, Long cartId, Long cardId) {
        orderService.createOrder(orderCreateRequest, cartId, cardId);
    }

    // 주문 회원별 조회 - 여러 주문이 있을 수 있다.
    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> orderFindMember(Long memberId) {
        orderService.orderFindMember(memberId);
        return null;
    }


}
