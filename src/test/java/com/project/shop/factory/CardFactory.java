package com.project.shop.factory;

import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.request.CardCreateRequest;

public class CardFactory {

    public static CardCreateRequest cardCreateRequest(Long memberId) {
        return CardCreateRequest.builder()
                .memberId(memberId)
                .cardCompany("국민은행")
                .cardNumber("1234")
                .cardExpire("11-11")
                .build();
    }
}
