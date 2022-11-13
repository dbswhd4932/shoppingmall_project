package com.project.shop.order.controller;

import com.project.shop.member.service.CartService;
import com.project.shop.member.service.Impl.CartServiceImpl;
import com.project.shop.order.domain.entity.request.OrderCreateRequest;
import com.project.shop.order.domain.entity.response.OrderResponse;
import com.project.shop.order.service.impl.OrderServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderServiceImpl orderService;

    //주문 생성
    @PostMapping("/orders")
    public void orderCreate(@RequestBody OrderCreateRequest orderCreateRequest, Long cartId) {
        orderService.createOrder(orderCreateRequest, cartId);
    }

    // 주문 회원별 조회
    @GetMapping("/orders")
    public List<OrderResponse> orderFind(Long memberId) {

        return null;
    }


}
