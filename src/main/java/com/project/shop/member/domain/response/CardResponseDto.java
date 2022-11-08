package com.project.shop.member.domain.response;

import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.entity.Member;
import lombok.*;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CardResponseDto {

    private Long memberId;
    private String cardCompany;
    private String cardNumber;
    private String cardExpire;

    public CardResponseDto(Card card){
        this.memberId = card.getMember().getId();
        this.cardCompany = card.getCardCompany();
        this.cardNumber = card.getCardNumber();
        this.cardExpire = card.getCardExpire();
    }

}
