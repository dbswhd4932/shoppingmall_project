package com.project.shop.order.service;

import com.project.shop.order.controller.request.PayCreateRequest;

public interface PayService {

    // 결제 생성
    void createPay(PayCreateRequest payCreateRequest);
}
