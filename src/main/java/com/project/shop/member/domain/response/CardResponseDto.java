package com.project.shop.member.domain.response;

import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardResponseDto {

    private Member member;
    private String cardCompany;
    private String cardNumber;
    private String cardExpire;

    @Builder
    public CardResponseDto(Member member, String cardCompany, String cardNumber, String cardExpire) {
        this.member = member;
        this.cardCompany = cardCompany;
        this.cardNumber = cardNumber;
        this.cardExpire = cardExpire;
    }

    public CardResponseDto(Card card){
        this.member = card.getMember();
        this.cardCompany = card.getCardCompany();
        this.cardNumber = card.getCardNumber();
        this.cardExpire = card.getCardExpire();
    }

}
