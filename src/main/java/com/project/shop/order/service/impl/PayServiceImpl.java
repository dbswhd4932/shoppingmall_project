package com.project.shop.order.service.impl;

import com.project.shop.global.error.ErrorCode;
import com.project.shop.global.error.exception.BusinessException;
import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.repository.CardRepository;
import com.project.shop.order.domain.entity.Order;
import com.project.shop.order.domain.entity.Pay;
import com.project.shop.order.domain.entity.request.PayCreateRequest;
import com.project.shop.order.repository.OrderRepository;
import com.project.shop.order.repository.PayRepository;
import com.project.shop.order.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PayServiceImpl implements PayService {

    private final PayRepository payRepository;
    private final OrderRepository orderRepository;
    private final CardRepository cardRepository;

    // 결제 생성
    @Override
    public void createPay(PayCreateRequest payCreateRequest) {
        Order order = orderRepository.findByMemberId(payCreateRequest.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하는 주문이 없습니다."));

        Card card = cardRepository.findById(payCreateRequest.getCardId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_CARD));

        if ( !card.getMember().getId().equals(payCreateRequest.getMemberId())) {
            throw new BusinessException(ErrorCode.NOT_FOUND_CARD);
        }

        Pay pay = Pay.builder()
                .cardId(card.getId())
                .price(order.getTotalPrice())
                .order(order)
                .build();

        payRepository.save(pay);
    }
}
