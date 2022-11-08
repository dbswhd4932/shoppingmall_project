package com.project.shop.order.controller;

import com.project.shop.order.domain.request.CreateOrderRequestDto;
import com.project.shop.order.domain.response.OrderResponseDto;
import com.project.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity<OrderResponseDto> order(@Valid @RequestBody CreateOrderRequestDto requestDto) {
        orderService.order(requestDto);
        return ResponseEntity.ok(new OrderResponseDto(requestDto));
    }

}
