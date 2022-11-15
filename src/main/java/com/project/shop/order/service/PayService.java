package com.project.shop.order.service;

import com.project.shop.order.domain.entity.request.PayCreateRequest;

public interface PayService {

    // 결제 생성
    void createPay(PayCreateRequest payCreateRequest);
}
