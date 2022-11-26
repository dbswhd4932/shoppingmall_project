package com.project.shop.member.controller.response;

import com.project.shop.member.domain.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponse {

    private Long memberId;
    private String cardCompany;
    private String cardNumber;
    private String cardExpire;

    public CardResponse(Card card) {
        this.memberId = card.getMember().getId();
        this.cardCompany = card.getCardCompany();
        this.cardNumber = card.getCardNumber();
        this.cardExpire = card.getCardExpire();
    }

}
