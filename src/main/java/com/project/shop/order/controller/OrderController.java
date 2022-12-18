package com.project.shop.order.controller;

import com.project.shop.order.controller.request.OrderCreateRequest;
import com.project.shop.order.controller.request.PayCancelRequest;
import com.project.shop.order.controller.response.OrderResponse;
import com.project.shop.order.domain.MerchantId;
import com.project.shop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    // MerchantID UUID 생성 - 프론트
    @GetMapping("/merchantId")
    public MerchantId merchantIdCreate() {
        return MerchantId.builder().merchantId(UUID.randomUUID()).build();
    }

    // 주문 생성
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER')")
    public void orderCreate(@RequestBody @Valid OrderCreateRequest orderCreateRequest) {
        orderService.cartOrder(orderCreateRequest);
    }

    // 주문 조회
    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER')")
    public List<OrderResponse> orderFindMember(Long memberId, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.orderFindMember(memberId, pageable);
    }

    // MerchantID 조회하기 - 프론트
    @GetMapping("/merchantId/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public String findMerchantId(@PathVariable("orderId") Long orderId) {
        return orderService.findMerchantId(orderId);
    }

    // 결제 취소
    @PostMapping("/payCancel/{payId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('USER')")
    public void payCancel(@PathVariable("payId") Long payId, @RequestBody PayCancelRequest payCancelRequest) {
        orderService.payCancel(payId, payCancelRequest);
    }
}
