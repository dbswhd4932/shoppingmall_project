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
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    // MerchantID UUID 생성
    @GetMapping("/merchantId")
    public MerchantId merchantIdCreate() {
        return MerchantId.builder()
                .merchantId(UUID.randomUUID())
                .build();
    }

    // 주문 생성
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public void orderCreate(@RequestBody @Valid OrderCreateRequest orderCreateRequest) {
        orderService.cartOrder(orderCreateRequest);
    }

    // 주문 회원별 조회 - 여러 주문이 있을 수 있다.
    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> orderFindMember(Long memberId, @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return orderService.orderFindMember(memberId, pageable);
    }

    // 가맹점 ID 조회하기
    @GetMapping("/merchantId/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public String findMerchantId(@PathVariable("orderId") Long orderId) {
        return orderService.findMerchantId(orderId);
    }

    // 결제 취소
    @PostMapping("/payCancel/{payId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void payCancel(@PathVariable("payId") Long payId,
                          @RequestBody PayCancelRequest payCancelRequest) {
        orderService.payCancel(payId, payCancelRequest);
    }
}
