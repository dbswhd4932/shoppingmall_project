package com.project.shop.member.domain.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CardCreateRequest {

    private Long memberId;
    private String cardCompany;
    private String cardNumber;
    private String cardExpire;

    @Builder
    public CardCreateRequest(Long memberId, String cardCompany, String cardNumber, String cardExpire) {
        this.memberId = memberId;
        this.cardCompany = cardCompany;
        this.cardNumber = cardNumber;
        this.cardExpire = cardExpire;
    }
}
