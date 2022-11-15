package com.project.shop.order.controller;

import com.project.shop.order.domain.entity.request.PayCreateRequest;
import com.project.shop.order.service.impl.PayServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class payController {

    private final PayServiceImpl payService;

    @PostMapping("/pay")
    @ResponseStatus(HttpStatus.CREATED)
    public void payCreate(@RequestBody PayCreateRequest payCreateRequest) {
        payService.createPay(payCreateRequest);
    }

}
