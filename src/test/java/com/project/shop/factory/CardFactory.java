package com.project.shop.factory;

import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.request.CardCreateDto;

public class CardFactory {

    public static CardCreateDto cardCreateDto() {
        return CardCreateDto.builder()
                .cardCompany("국민은행")
                .cardNumber("1234")
                .cardExpire("11-11")
                .build();
    }

    public static Card createCard() {
        return Card.builder()
                .cardCompany("우리")
                .cardNumber("1234")
                .cardExpire("11-11")
                .build();
    }
}
