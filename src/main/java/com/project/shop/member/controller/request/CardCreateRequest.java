package com.project.shop.member.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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
