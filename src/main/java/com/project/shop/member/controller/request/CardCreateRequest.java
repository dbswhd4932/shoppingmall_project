package com.project.shop.member.controller.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class CardCreateRequest {

    @NotNull(message = "회원번호를 입력하세요.")
    private Long memberId;

    @NotNull(message = "카드회사를 입력하세요.")
    private String cardCompany;

    @NotNull(message = "카드번호를 입력하세요.")
    private String cardNumber;

    @NotNull(message = "카드 만료기한을 입력하세요.")
    private String cardExpire;

    @Builder
    public CardCreateRequest(Long memberId, String cardCompany, String cardNumber, String cardExpire) {
        this.memberId = memberId;
        this.cardCompany = cardCompany;
        this.cardNumber = cardNumber;
        this.cardExpire = cardExpire;
    }
}
