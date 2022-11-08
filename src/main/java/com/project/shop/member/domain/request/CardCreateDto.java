package com.project.shop.member.domain.request;

import com.project.shop.member.domain.entity.Card;
import com.project.shop.member.domain.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CardCreateDto {

    // todo Long memberId 로 찾아야하나??
    private Member member;
    private String cardCompany;
    private String cardNumber;
    private String cardExpire;

    @Builder
    public CardCreateDto(String cardCompany, String cardNumber, String cardExpire) {
        this.cardCompany = cardCompany;
        this.cardNumber = cardNumber;
        this.cardExpire = cardExpire;
    }


}
