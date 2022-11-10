package com.project.shop.member.domain.response;

import com.project.shop.member.domain.entity.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
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
