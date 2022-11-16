package com.project.shop.factory;

import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.entity.Member;
import com.project.shop.member.domain.request.CardCreateRequest;

public class CardFactory {

    public static CardCreateRequest cardCreateRequest(Member member) {
        return CardCreateRequest.builder()
                .memberId(member.getId())
                .cardCompany("국민은행")
                .cardNumber("1234")
                .cardExpire("11-11")
                .build();
    }

    public static Card cardCreate(Member member) {
        return Card.builder()
                .member(member)
                .cardCompany("국민")
                .cardNumber("1234")
                .cardExpire("12-12")
                .build();
    }
}
